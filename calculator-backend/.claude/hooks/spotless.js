// PostToolUse hook: format an edited backend .java file with Spotless.
const path = require('path');
const { execSync } = require('child_process');

const backend = path.resolve(__dirname, '..', '..');
let d = '';
process.stdin.on('data', c => (d += c));
process.stdin.on('end', () => {
  const f = path.resolve(((JSON.parse(d).tool_input) || {}).file_path || '');
  if (!f.endsWith('.java') || !f.toLowerCase().startsWith(backend.toLowerCase() + path.sep)) return;
  const regex = f.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const mvnw = '"' + path.join(backend, process.platform === 'win32' ? 'mvnw.cmd' : 'mvnw') + '"';
  try {
    execSync(`${mvnw} -q spotless:apply "-DspotlessFiles=${regex}"`, { cwd: backend, stdio: 'pipe' });
  } catch (e) {
    const out = ((e.stdout || '') + (e.stderr || '')).toString().trim().split('\n').slice(-5).join('\n');
    process.stdout.write(JSON.stringify({ systemMessage: 'Spotless failed: ' + out }));
  }
});
