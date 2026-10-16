import { copyFile, readFile, writeFile } from 'node:fs/promises';
import { spawnSync } from 'node:child_process';
import { join } from 'node:path';

const projectVersion = process.env.MAVEN_PROJECT_VERSION;
const buildTimestamp = process.env.MAVEN_BUILD_TIMESTAMP;
const targetDirectory = process.env.MAVEN_TARGET_DIRECTORY;
const npmCli = process.env.npm_execpath;

if (!projectVersion || !buildTimestamp || !targetDirectory || !npmCli) {
  throw new Error('MAVEN_PROJECT_VERSION, MAVEN_BUILD_TIMESTAMP, MAVEN_TARGET_DIRECTORY, and npm_execpath are required');
}

const versionMatch = /^(\d+)\.(\d+)(?:\.(\d+))?(-SNAPSHOT)?$/.exec(projectVersion);
if (!versionMatch) {
  throw new Error(`Unsupported Maven project version: ${projectVersion}`);
}

const [, major, minor, patch = '0', snapshot] = versionMatch;
let npmVersion = `${major}.${minor}.${patch}`;
if (snapshot) {
  const now = new Date(buildTimestamp);
  if (Number.isNaN(now.getTime())) {
    throw new Error(`Invalid Maven build timestamp: ${buildTimestamp}`);
  }
  const timestamp = [
    now.getUTCFullYear(),
    String(now.getUTCMonth() + 1).padStart(2, '0'),
    String(now.getUTCDate()).padStart(2, '0'),
    String(now.getUTCHours()).padStart(2, '0'),
    String(now.getUTCMinutes()).padStart(2, '0'),
    String(now.getUTCSeconds()).padStart(2, '0'),
  ].join('');
  npmVersion += `-snapshot.${timestamp}`;
}

const distribution = join(targetDirectory, 'npm-dist');
const manifestPath = join(distribution, 'package.json');
const manifest = JSON.parse(await readFile(manifestPath, 'utf8'));
manifest.version = npmVersion;
await writeFile(manifestPath, `${JSON.stringify(manifest, null, 2)}\n`);

const packed = spawnSync(process.execPath, [npmCli, 'pack', '--json', '--pack-destination', targetDirectory], {
  cwd: distribution,
  encoding: 'utf8',
  stdio: ['ignore', 'pipe', 'inherit'],
});
if (packed.status !== 0) {
  throw new Error(`npm pack failed with exit code ${packed.status}`);
}

const packResult = JSON.parse(packed.stdout);
const packages = Array.isArray(packResult) ? packResult : Object.values(packResult);
if (packages.length !== 1 || typeof packages[0]?.filename !== 'string') {
  throw new Error('npm pack must return exactly one package with a filename');
}
const [{ filename }] = packages;
await copyFile(join(targetDirectory, filename), join(targetDirectory, 'bonaparte-api-npm.tgz'));
console.log(`Packed ${manifest.name}@${npmVersion}.`);