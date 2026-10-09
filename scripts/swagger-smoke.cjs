// npm install --prefix .tools --no-audit --no-fund playwright
// 실행 중인 서버에 대해: node scripts/swagger-smoke.cjs
const { chromium } = require('../.tools/node_modules/playwright');
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');

async function main() {
    const baseUrl = process.env.BASE_URL || 'http://localhost:8080';
    const output = path.resolve(__dirname, '../docs/screenshots');
    fs.mkdirSync(output, { recursive: true });
    const browser = await chromium.launch({ channel: 'chrome', headless: true });
    const page = await browser.newPage({ viewport: { width: 1280, height: 1000 } });
    const results = [];
    try {
        await page.goto(`${baseUrl}/swagger-ui/index.html`);
        await page.locator('.opblock').first().waitFor();
        async function call(method, route, expected, { body, id, screenshot } = {}) {
            const block = page.locator(`.opblock.opblock-${method.toLowerCase()}`)
                .filter({ has: page.locator(`.opblock-summary-path[data-path="${route}"]`) });
            if (!(await block.locator('.opblock-body').isVisible())) {
                await block.locator('.opblock-summary').click();
            }
            await block.locator('.opblock-body').waitFor();
            const tryButton = block.getByRole('button', { name: 'Try it out', exact: true });
            if (!(await block.getByRole('button', { name: 'Cancel', exact: true }).isVisible())) {
                await tryButton.waitFor();
                await tryButton.click();
            }
            if (id !== undefined) await block.locator('input[placeholder="id"]').fill(String(id));
            if (body !== undefined) await block.locator('textarea').fill(JSON.stringify(body, null, 2));
            const requestPath = route.replace('{id}', String(id));
            const responsePromise = page.waitForResponse(response =>
                new URL(response.url()).pathname === requestPath && response.request().method() === method);
            await block.getByRole('button', { name: 'Execute', exact: true }).click();
            const response = await responsePromise;
            assert.equal(response.status(), expected, `${method} ${requestPath}`);
            const data = expected === 204 ? null : await response.json();
            await block.locator('.live-responses-table .response-col_status')
                .filter({ hasText: String(expected) }).waitFor();
            if (screenshot) {
                await block.screenshot({ path: path.join(output, screenshot), animations: 'disabled' });
            }
            results.push({ method, path: requestPath, status: response.status(), response: data });
            console.log(`${method} ${requestPath}: ${response.status()}`);
            return data;
        }

        const userBody = { email: `sua-${Date.now()}@example.com`, password: 'practice-password' };
        const user = await call('POST', '/users', 201, { body: userBody, screenshot: '01-user-create-201.png' });
        assert.equal(user.email, userBody.email);
        assert.ok(user.createdAt);
        assert.equal(Object.hasOwn(user, 'password'), false);
        await call('POST', '/users', 409, { body: userBody, screenshot: '12-duplicate-email-409.png' });
        const book = await call('POST', '/books', 201, { body: { title: '자바의 정석', author: '남궁성' }, screenshot: '02-book-create-201.png' });
        const body = { userId: user.id, bookId: book.id };
        const loan = await call('POST', '/loans', 201, { body, screenshot: '03-loan-create-201.png' });
        const dueDate = new Date(`${loan.loanDate}T00:00:00Z`);
        dueDate.setUTCDate(dueDate.getUTCDate() + 14);
        assert.equal(loan.dueDate, dueDate.toISOString().slice(0, 10));
        let state = await call('GET', '/books/{id}', 200, { id: book.id, screenshot: '04-book-borrowed-true.png' });
        assert.equal(state.isBorrowed, true);
        await call('POST', '/loans', 409, { body, screenshot: '05-duplicate-loan-409.png' });
        const list = await call('GET', '/loans', 200, { screenshot: '06-loan-list-200.png' });
        assert.ok(list.some(item => item.id === loan.id));
        await call('GET', '/loans/{id}', 200, { id: loan.id, screenshot: '07-loan-detail-200.png' });
        const returned = await call('PUT', '/loans/{id}', 200, { id: loan.id, screenshot: '08-loan-return-200.png' });
        assert.ok(returned.returnDate);
        assert.equal(returned.dueDate, loan.dueDate);
        state = await call('GET', '/books/{id}', 200, { id: book.id, screenshot: '09-book-borrowed-false.png' });
        assert.equal(state.isBorrowed, false);
        await call('PUT', '/loans/{id}', 409, { id: loan.id });
        const newLoan = await call('POST', '/loans', 201, { body });
        await call('DELETE', '/loans/{id}', 204, { id: loan.id, screenshot: '10-loan-delete-204.png' });
        state = await call('GET', '/books/{id}', 200, { id: book.id });
        assert.equal(state.isBorrowed, true);
        await call('DELETE', '/loans/{id}', 204, { id: newLoan.id });
        state = await call('GET', '/books/{id}', 200, { id: book.id });
        assert.equal(state.isBorrowed, false);
        await call('GET', '/loans/{id}', 404, { id: loan.id, screenshot: '11-deleted-loan-404.png' });
        const userList = await call('GET', '/users', 200);
        assert.ok(userList.every(item => !Object.hasOwn(item, 'password')));
        const userDetail = await call('GET', '/users/{id}', 200, { id: user.id });
        assert.equal(Object.hasOwn(userDetail, 'password'), false);
        await call('GET', '/books', 200);
        await call('DELETE', '/books/{id}', 204, { id: book.id });
        await call('DELETE', '/users/{id}', 204, { id: user.id });
        fs.writeFileSync(path.join(output, 'results.json'), JSON.stringify({ baseUrl, checkedAt: new Date().toISOString(), results }, null, 2));
        console.log(`Saved screenshots and ${results.length} verified Swagger calls to ${output}`);
    } finally {
        await browser.close();
    }
}

main().catch(error => { console.error(error); process.exitCode = 1; });
