#!/usr/bin/env python3
"""Compare Stonecutter output with the previous shared-source branch."""
import argparse
import json
from pathlib import Path
import shutil
import subprocess
import tarfile


def read_baseline(root, ref):
    files = {}
    with subprocess.Popen(['git', 'archive', ref, '--', 'common', 'shared', 'versions',
                           'gradle/source-groups.json'], cwd=root, stdout=subprocess.PIPE) as process:
        with tarfile.open(fileobj=process.stdout, mode='r|') as archive:
            for entry in archive:
                if entry.isfile() and ('/src/' in entry.name or entry.name == 'gradle/source-groups.json'):
                    files[entry.name] = archive.extractfile(entry).read()
        if process.wait() != 0:
            raise ValueError(f'Cannot read shared-source baseline {ref}')
    return files


def collect_baseline(files, version):
    groups = json.loads(files['gradle/source-groups.json'])
    prefixes = [f'versions/{version}/src/']
    prefixes += [group + '/src/' for group, members in groups.items() if version in members]
    result = {}
    for prefix in prefixes:
        for path, content in files.items():
            if path.startswith(prefix):
                relative = path[len(prefix):]
                if relative in result:
                    raise ValueError(f'Duplicate baseline source {version}: {relative}')
                result[relative] = content
    return result


def main():
    root = Path(__file__).resolve().parents[1]
    versions = [v['minecraft'] for v in json.loads((root / 'versions.json').read_text())['versions']]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--version', choices=versions, required=True)
    parser.add_argument('--baseline-ref', default='99e41b0b3335a18a368c3495194dc92314c355f4')
    parser.add_argument('--production-only', action='store_true', help='Allow test Java to follow the new source layout; all resources are still compared')
    args = parser.parse_args()
    files = collect_baseline(read_baseline(root, args.baseline_ref), args.version)
    destination = root / 'build' / 'parity' / args.version / 'expected'
    shutil.rmtree(destination, ignore_errors=True)
    for relative, content in files.items():
        path = destination / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(content)
    generated = root / 'build' / 'stonecutter' / args.version / 'generated' / 'stonecutter'
    effective = destination.parent / 'actual'
    shutil.rmtree(effective, ignore_errors=True)
    shutil.copytree(generated, effective)
    for path in (root / 'versions' / args.version / 'src').rglob('*'):
        if path.is_file():
            relative = path.relative_to(root / 'versions' / args.version / 'src')
            target = effective / relative
            if target.exists():
                raise ValueError(f'Duplicate generated/local source: {relative}')
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(path, target)
    command = ['java', str(root / 'scripts/SourceParity.java'), str(destination), str(effective)]
    if args.production_only:
        command.append('--production-only')
    subprocess.run(command, cwd=root, check=True)


if __name__ == '__main__':
    main()
