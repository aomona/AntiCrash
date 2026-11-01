import json
import unittest
from check_sources import collect_baseline


class BaselineTest(unittest.TestCase):
    def test_membership_and_version_resources(self):
        files = {'gradle/source-groups.json': json.dumps({'common': ['old', 'new'], 'shared/old': ['old']}).encode(),
                 'common/src/main/java/Common.java': b'class Common {}',
                 'shared/old/src/main/java/OldApi.java': b'class OldApi {}',
                 'versions/new/src/main/resources/fabric.mod.json': b'{}'}
        self.assertEqual({'main/java/Common.java', 'main/resources/fabric.mod.json'}, set(collect_baseline(files, 'new')))
        self.assertEqual({'main/java/Common.java', 'main/java/OldApi.java'}, set(collect_baseline(files, 'old')))

    def test_duplicate_source_rejected(self):
        files = {'gradle/source-groups.json': b'{"common": ["new"]}',
                 'common/src/main/java/Example.java': b'class Example {}',
                 'versions/new/src/main/java/Example.java': b'class Example {}'}
        with self.assertRaisesRegex(ValueError, 'Duplicate baseline source'):
            collect_baseline(files, 'new')
