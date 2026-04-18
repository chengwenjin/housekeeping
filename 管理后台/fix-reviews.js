const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const mysqlPath = 'D:/laragon/bin/mysql/mysql-8.4.3-winx64/bin/mysql.exe';
const dbName = 'jiazheng_miniapp_new';
const host = 'localhost';
const user = 'root';

const reviewContents = [
    { rating: 5, content: '服务非常专业，准时到达，态度很好，强烈推荐！' },
    { rating: 5, content: '师傅技术很好，干活认真仔细，下次还会再找他。' },
    { rating: 5, content: '非常满意的一次服务体验，师傅很有耐心，把家里打扫得干干净净。' },
    { rating: 5, content: '预约的时间很准时，师傅干活麻利，效果超出预期，好评！' },
    { rating: 5, content: '服务态度好，工作认真负责，价格合理，值得信赖！' },
    { rating: 5, content: '师傅很专业，提前沟通确认需求，服务过程很顺利。' },
    { rating: 5, content: '非常棒的服务，师傅经验丰富，很快就解决了我的问题。' },
    { rating: 5, content: '服务质量很好，师傅手脚麻利，人也很nice，推荐给大家！' },
    { rating: 5, content: '整体体验非常好，从预约到服务完成都很顺畅，五星好评！' },
    { rating: 5, content: '师傅很敬业，干活细致，效果满意，以后会继续使用。' },
    { rating: 4, content: '整体还不错，师傅服务态度好，就是稍微晚了一点点。' },
    { rating: 4, content: '服务质量可以，师傅很认真，时间安排合理。' },
    { rating: 4, content: '比较满意的一次服务，师傅技术不错，沟通也顺畅。' },
    { rating: 4, content: '服务过程很顺利，师傅很专业，只是价格稍贵了一点。' },
    { rating: 4, content: '整体体验良好，师傅做事认真，值得推荐。' },
    { rating: 4, content: '师傅很准时，干活也不错，就是细节处理可以再改进一下。' },
    { rating: 4, content: '服务态度很好，工作也比较细致，总体满意。' },
    { rating: 4, content: '师傅经验丰富，很快就完成了工作，只是效果基本符合预期。' },
    { rating: 3, content: '服务一般般吧，没有想象中的好，也不算太差。' },
    { rating: 3, content: '师傅准时到了，就是干活速度有点慢，整体还行。' },
    { rating: 3, content: '服务基本达标，态度还可以，就是效果一般。' },
    { rating: 3, content: '预约过程比较顺利，服务质量中规中矩。' },
    { rating: 3, content: '没有特别满意的地方，也没有太失望的地方。' }
];

function escapeSql(str) {
    if (str === null || str === undefined) return 'NULL';
    if (typeof str === 'string') {
        return "'" + str.replace(/'/g, "''").replace(/\\/g, "\\\\") + "'";
    }
    return str;
}

console.log('============================================');
console.log('生成评价数据（使用 Node.js 确保编码正确）');
console.log('============================================');
console.log('');

console.log('[1/4] 清空旧的评价数据...');
const deleteSql = `SET NAMES utf8mb4; DELETE FROM reviews WHERE id > 0;`;
try {
    execSync(`"${mysqlPath}" -h ${host} -u ${user} --default-character-set=utf8mb4 ${dbName} -e ${escapeSql(deleteSql)}`);
    console.log('✅ 已清空旧评价数据');
} catch (e) {
    console.log('⚠️  清空数据时出错（可能表是空的）:', e.message);
}

console.log('');
console.log('[2/4] 查询需要生成评价的订单...');

const queryOrdersSql = `
SET NAMES utf8mb4;
SELECT 
    o.id as order_id,
    o.demand_id,
    o.publisher_id,
    o.taker_id,
    o.completed_at
FROM orders o
LEFT JOIN reviews r ON o.id = r.order_id
WHERE o.status >= 3 
  AND r.id IS NULL
  AND o.publisher_id IS NOT NULL 
  AND o.taker_id IS NOT NULL
ORDER BY o.id;
`;

let orders = [];
try {
    const output = execSync(`"${mysqlPath}" -h ${host} -u ${user} --default-character-set=utf8mb4 -N ${dbName} -e ${escapeSql(queryOrdersSql)}`, { encoding: 'utf8' });
    const lines = output.trim().split('\n').filter(line => line.trim());
    orders = lines.map(line => {
        const parts = line.split('\t');
        return {
            order_id: parseInt(parts[0]),
            demand_id: parseInt(parts[1]),
            publisher_id: parseInt(parts[2]),
            taker_id: parseInt(parts[3]),
            completed_at: parts[4] || null
        };
    });
    console.log(`✅ 找到 ${orders.length} 个需要生成评价的订单`);
} catch (e) {
    console.log('❌ 查询订单失败:', e.message);
    process.exit(1);
}

console.log('');
console.log('[3/4] 生成并插入评价数据...');

let inserted = 0;
const batchSize = 10;

for (let i = 0; i < orders.length; i += batchSize) {
    const batchOrders = orders.slice(i, i + batchSize);
    const values = batchOrders.map(order => {
        const rand = Math.random();
        let rating;
        if (rand < 0.6) rating = 5;
        else if (rand < 0.9) rating = 4;
        else rating = 3;
        
        const contents = reviewContents.filter(c => c.rating === rating);
        const content = contents[Math.floor(Math.random() * contents.length)];
        const isAnonymous = Math.random() < 0.1 ? 1 : 0;
        const helpfulCount = Math.floor(Math.random() * 10);
        
        let createdAt;
        if (order.completed_at) {
            const days = Math.floor(1 + Math.random() * 2);
            createdAt = new Date(new Date(order.completed_at).getTime() + days * 24 * 60 * 60 * 1000);
        } else {
            const days = Math.floor(1 + Math.random() * 30);
            createdAt = new Date(Date.now() - days * 24 * 60 * 60 * 1000);
        }
        
        const createdAtStr = createdAt.toISOString().slice(0, 19).replace('T', ' ');
        
        return `(
            ${order.order_id},
            ${order.demand_id},
            ${order.publisher_id},
            ${order.taker_id},
            1,
            ${rating},
            ${escapeSql(content.content)},
            ${isAnonymous},
            ${helpfulCount},
            '${createdAtStr}',
            '${createdAtStr}'
        )`;
    }).join(',\n');
    
    const insertSql = `
SET NAMES utf8mb4;
INSERT INTO reviews (
    order_id,
    demand_id,
    reviewer_id,
    reviewee_id,
    review_type,
    rating,
    content,
    is_anonymous,
    helpful_count,
    created_at,
    updated_at
) VALUES ${values};
`;

    try {
        execSync(`"${mysqlPath}" -h ${host} -u ${user} --default-character-set=utf8mb4 ${dbName} -e ${escapeSql(insertSql)}`);
        inserted += batchOrders.length;
        process.stdout.write(`\r   已插入 ${inserted}/${orders.length} 条评价...`);
    } catch (e) {
        console.log('');
        console.log('❌ 插入数据失败:', e.message);
        console.log('SQL:', insertSql);
        process.exit(1);
    }
}

console.log('');
console.log('✅ 评价数据插入完成');

console.log('');
console.log('[4/4] 验证数据...');

const verifySql = `
SET NAMES utf8mb4;
SELECT 
    COUNT(*) as total,
    AVG(rating) as avg_rating
FROM reviews;

SELECT id, order_id, rating, LEFT(content, 40) as content_preview 
FROM reviews 
ORDER BY id DESC 
LIMIT 10;
`;

try {
    const output = execSync(`"${mysqlPath}" -h ${host} -u ${user} --default-character-set=utf8mb4 ${dbName} -e ${escapeSql(verifySql)}`, { encoding: 'utf8' });
    console.log(output);
} catch (e) {
    console.log('❌ 验证数据失败:', e.message);
}

console.log('');
console.log('============================================');
console.log('✅ 完成！共生成 ' + inserted + ' 条评价数据');
console.log('============================================');
