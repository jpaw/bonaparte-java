import { cpSync, existsSync, mkdirSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { XMLParser } from 'fast-xml-parser';

const packageRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const reactorRoot = path.resolve(packageRoot, '../../../../');
const targetDirectory = path.resolve(packageRoot, '../../../target');
const manifestPath = path.join(targetDirectory, 'generated-ts-entrypoints.json');
const parser = new XMLParser({ removeNSPrefix: true, isArray: (name) => name === 'dependency' });
const pom = parser.parse(readFileSync(path.join(reactorRoot, 'bonaparte-api-ts/pom.xml'), 'utf8'));
const dependencies = pom.project.dependencies?.dependency ?? [];
const modules = dependencies.map(({ artifactId }) => artifactId);
if (modules.length === 0 || new Set(modules).size !== modules.length) {
  throw new Error('Packaging POM must declare a nonempty, unique set of module dependencies.');
}

if (existsSync(manifestPath)) {
  for (const entryPoint of JSON.parse(readFileSync(manifestPath, 'utf8'))) {
    rmSync(path.join(packageRoot, entryPoint), { recursive: true, force: true });
  }
}

for (const moduleName of modules) {
  const source = path.join(reactorRoot, moduleName, 'src/generated/ts', moduleName);
  if (!existsSync(path.join(source, 'ng-package.json'))) {
    throw new Error(`Missing generated TypeScript entry point for ${moduleName}: ${source}`);
  }
  const entryRoot = path.join(packageRoot, moduleName);
  rmSync(entryRoot, { recursive: true, force: true });
  cpSync(source, entryRoot, { recursive: true });
}

mkdirSync(targetDirectory, { recursive: true });
writeFileSync(manifestPath, `${JSON.stringify(modules, null, 2)}\n`);
console.log(`Assembled ${modules.length} Bonaparte TypeScript entry points from Maven dependencies.`);