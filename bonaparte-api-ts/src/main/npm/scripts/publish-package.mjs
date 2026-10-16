import { readFile, unlink, writeFile } from 'node:fs/promises';
import { spawnSync } from 'node:child_process';
import { join } from 'node:path';

const token = process.env.NODE_AUTH_TOKEN;
const targetDirectory = process.env.MAVEN_TARGET_DIRECTORY;
const npmCli = process.env.npm_execpath;
const registryUrl = process.env.NPM_REGISTRY_URL || 'https://npm.pkg.github.com';

function isLoopbackRegistry(url) {
  const parsed = new URL(url);
  return parsed.protocol === 'http:' && ['localhost', '127.0.0.1'].includes(parsed.hostname);
}

const localRegistry = isLoopbackRegistry(registryUrl);
if ((!token && !localRegistry) || !targetDirectory || !npmCli) {
  throw new Error('A NODE_AUTH_TOKEN is required except for a loopback registry; MAVEN_TARGET_DIRECTORY and npm_execpath are always required');
}

const distribution = join(targetDirectory, 'npm-dist');
const manifest = JSON.parse(await readFile(join(distribution, 'package.json'), 'utf8'));
const npmrc = join(distribution, '.npmrc');
await writeFile(npmrc, `//${new URL(registryUrl).host}/:_authToken=${token || 'local-development-only'}\n`, { mode: 0o600 });

try {
  const tag = manifest.version.includes('-snapshot.') ? 'snapshot' : 'latest';
  const publishArgs = [npmCli, 'publish', `--registry=${registryUrl}`, `--tag=${tag}`];
  if (!localRegistry) {
    publishArgs.push('--access=restricted');
  }
  const published = spawnSync(process.execPath, publishArgs, { cwd: distribution, stdio: 'inherit' });
  if (published.status !== 0) {
    throw new Error(`npm publish failed with exit code ${published.status}`);
  }
} finally {
  await unlink(npmrc).catch(() => {});
}