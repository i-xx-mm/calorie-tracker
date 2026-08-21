/**
 * Mock Backend Server for Testing
 * Simulates the Calorie Tracker API without needing Java/MongoDB
 */

const http = require('http');
const url = require('url');
const fs = require('fs');
const path = require('path');

// Data persistence file
const DATA_FILE = path.join(__dirname, 'mock-data.json');

// In-memory database
const db = {
  users: new Map(),
  foods: new Map(),      // Custom food database: id -> { id, name (lowercase), calorie, note, createdAt }
  foodLogs: new Map(),
  nextFoodId: 1,
  nextFoodLogId: 1
};

// Load data from file
function loadData() {
  try {
    if (fs.existsSync(DATA_FILE)) {
      const data = JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
      
      // Restore users
      if (data.users) {
        data.users.forEach(user => {
          db.users.set(user.username, user);
        });
      }
      
      // Restore foods
      if (data.foods) {
        data.foods.forEach(food => {
          db.foods.set(food.id, food);
        });
      }
      
      // Restore food logs
      if (data.foodLogs) {
        data.foodLogs.forEach(log => {
          db.foodLogs.set(parseInt(log.id), log);
        });
      }
      
      // Restore counters
      if (data.nextFoodId) db.nextFoodId = data.nextFoodId;
      if (data.nextFoodLogId) db.nextFoodLogId = data.nextFoodLogId;
      
      console.log('📂 Loaded persisted data from file');
    }
  } catch (err) {
    console.error('⚠️  Error loading data:', err.message);
  }
}

// Save data to file
function saveData() {
  try {
    const data = {
      users: Array.from(db.users.values()),
      foods: Array.from(db.foods.values()),
      foodLogs: Array.from(db.foodLogs.values()),
      nextFoodId: db.nextFoodId,
      nextFoodLogId: db.nextFoodLogId,
      savedAt: new Date().toISOString()
    };
    
    fs.writeFileSync(DATA_FILE, JSON.stringify(data, null, 2), 'utf8');
  } catch (err) {
    console.error('⚠️  Error saving data:', err.message);
  }
}

// Seed common foods into the database
function seedFoods() {
  const commonFoods = [
    { id: 1, name: 'apple', calorie: 95, note: 'medium sized' },
    { id: 2, name: 'apple', calorie: 52, note: 'small sized' },
    { id: 3, name: 'banana', calorie: 105, note: 'medium sized' },
    { id: 4, name: 'banana', calorie: 121, note: 'large sized' },
    { id: 5, name: 'orange', calorie: 62, note: 'medium sized' },
    { id: 6, name: 'chicken breast', calorie: 165, note: 'cooked, skinless, 100g' },
    { id: 7, name: 'chicken breast', calorie: 330, note: 'cooked, skinless, 200g' },
    { id: 8, name: 'rice', calorie: 130, note: 'cooked white rice, 100g' },
    { id: 9, name: 'rice', calorie: 206, note: 'cooked brown rice, 100g' },
    { id: 10, name: 'bread', calorie: 79, note: 'white bread, 1 slice' },
    { id: 11, name: 'bread', calorie: 88, note: 'wheat bread, 1 slice' },
    { id: 12, name: 'egg', calorie: 155, note: 'large boiled' },
    { id: 13, name: 'milk', calorie: 149, note: 'whole milk, 1 cup' },
    { id: 14, name: 'milk', calorie: 103, note: 'skim milk, 1 cup' },
    { id: 15, name: 'broccoli', calorie: 55, note: 'raw, chopped, 1 cup' },
    { id: 16, name: 'carrot', calorie: 25, note: 'raw, 1 medium' },
    { id: 17, name: 'yogurt', calorie: 100, note: 'plain, 100g' },
    { id: 18, name: 'cheese', calorie: 402, note: 'cheddar, 100g' },
    { id: 19, name: 'pasta', calorie: 131, note: 'cooked, 100g' },
    { id: 20, name: 'salmon', calorie: 280, note: 'cooked, 100g' }
  ];
  
  commonFoods.forEach(food => {
    db.foods.set(food.id, {
      id: food.id,
      name: food.name.toLowerCase(),
      calorie: food.calorie,
      note: food.note,
      createdAt: new Date().toISOString()
    });
  });
  
  db.nextFoodId = 21;
}

// Seed mock monthly data for visualization
function seedMockMonthlyData() {
  // Create a test user
  const testUsername = 'testuser';
  const testUser = {
    username: testUsername,
    password: 'password123',
    height: 175,
    weight: 70,
    age: 28,
    gender: 'male'
  };
  db.users.set(testUsername, testUser);

  // Create mock food logs for the past 20 days using EST timezone
  const todayEST = getTodayEST();
  const foodIds = [1, 3, 5, 6, 8, 10, 12, 13, 15, 17]; // Random food IDs
  
  for (let i = 20; i >= 0; i--) {
    // Calculate date using EST
    const estDate = new Date();
    const currentEST = new Date(estDate.toLocaleString('en-US', { timeZone: 'America/New_York' }));
    currentEST.setDate(currentEST.getDate() - i);
    const year = currentEST.getFullYear();
    const month = String(currentEST.getMonth() + 1).padStart(2, '0');
    const day = String(currentEST.getDate()).padStart(2, '0');
    const dateStr = `${year}-${month}-${day}`;
    
    // Generate 2-4 random food entries per day
    const numFoods = Math.floor(Math.random() * 3) + 2;
    const foods = [];
    let totalCalories = 0;
    
    for (let j = 0; j < numFoods; j++) {
      const randomFoodId = foodIds[Math.floor(Math.random() * foodIds.length)];
      const food = db.foods.get(randomFoodId);
      
      if (food) {
        foods.push({
          id: Date.now().toString() + j,
          name: food.name,
          calorie: food.calorie,
          note: food.note
        });
        totalCalories += food.calorie;
      }
    }
    
    // Vary calorie intake between 1500-2500
    const variance = Math.floor(Math.random() * 1000) - 500;
    totalCalories = Math.max(1200, Math.min(2800, totalCalories + variance));
    
    const logId = db.nextFoodLogId++;
    const foodLog = {
      id: logId.toString(),
      username: testUsername,
      date: dateStr,
      foods: foods,
      totalCalories: totalCalories
    };
    
    db.foodLogs.set(logId, foodLog);
  }
  
  console.log(`📊 Seeded ${db.foodLogs.size} mock food logs for visualization`);
}

// Mock JWT secret
const JWT_SECRET = 'test-secret-key-for-development-min-32-chars-long';

// Helper function to get current date in EST timezone (YYYY-MM-DD format)
function getTodayEST() {
  const now = new Date();
  const estDate = new Date(now.toLocaleString('en-US', { timeZone: 'America/New_York' }));
  const year = estDate.getFullYear();
  const month = String(estDate.getMonth() + 1).padStart(2, '0');
  const day = String(estDate.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

// Helper function to get current month in EST (YYYY-MM format)
function getCurrentMonthEST() {
  return getTodayEST().substring(0, 7);
}

// Simple JWT encode (not cryptographically secure, just for testing)
function createToken(username) {
  const header = Buffer.from(JSON.stringify({ alg: 'HS256', typ: 'JWT' })).toString('base64');
  const payload = Buffer.from(JSON.stringify({ sub: username, iat: Date.now() })).toString('base64');
  return `${header}.${payload}.signature`;
}

// Request handler
function handleRequest(req, res) {
  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:4200');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  res.setHeader('Content-Type', 'application/json');

  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;
  const method = req.method;

  console.log(`${method} ${pathname}`);

  // Route handlers
  if (pathname === '/api/auth/register' && method === 'POST') {
    handleRegister(req, res);
  } else if (pathname === '/api/auth/login' && method === 'POST') {
    handleLogin(req, res);
  } else if (pathname === '/api/users/me' && method === 'GET') {
    handleGetUser(req, res);
  } else if (pathname === '/api/users/me' && method === 'PUT') {
    handleUpdateUser(req, res);
  } else if (pathname === '/api/foods' && method === 'GET') {
    handleSearchFoods(req, res);
  } else if (pathname === '/api/foods' && method === 'POST') {
    handleAddFood(req, res);
  } else if (pathname.match(/^\/api\/foods\/\d+/) && method === 'PUT') {
    handleUpdateFood(req, res);
  } else if (pathname.match(/^\/api\/foods\/\d+/) && method === 'DELETE') {
    handleDeleteFood(req, res);
  } else if (pathname === '/api/foodlogs' && method === 'GET') {
    handleGetFoodLogs(req, res);
  } else if (pathname === '/api/foodlogs' && method === 'POST') {
    handleAddFoodLog(req, res);
  } else if (pathname.match(/^\/api\/foodlogs\/\d+/) && method === 'PUT') {
    handleUpdateFoodLog(req, res);
  } else if (pathname.match(/^\/api\/foodlogs\/\d+/) && method === 'DELETE') {
    handleDeleteFoodLog(req, res);
  } else if (pathname === '/api/dashboard/today' && method === 'GET') {
    handleDashboardToday(req, res);
  } else if (pathname === '/api/dashboard/monthly-stats' && method === 'GET') {
    handleMonthlyStats(req, res);
  } else {
    res.writeHead(404);
    res.end(JSON.stringify({ error: 'Not found' }));
  }
}

function readBody(req, callback) {
  let data = '';
  req.on('data', chunk => data += chunk);
  req.on('end', () => {
    try {
      callback(JSON.parse(data || '{}'));
    } catch (e) {
      callback({});
    }
  });
}

function handleRegister(req, res) {
  readBody(req, (body) => {
    const { username, password, height, weight, age, gender } = body;
    
    if (!username || !password) {
      res.writeHead(400);
      return res.end(JSON.stringify({ error: 'Missing fields' }));
    }

    if (db.users.has(username)) {
      res.writeHead(409);
      return res.end(JSON.stringify({ error: 'User already exists' }));
    }

    db.users.set(username, { username, password, height, weight, age, gender });
    const token = createToken(username);
    
    saveData();

    res.writeHead(201);
    res.end(JSON.stringify({
      token,
      username,
      expiresIn: 86400000
    }));
  });
}

function handleLogin(req, res) {
  readBody(req, (body) => {
    const { username, password } = body;
    
    if (!username || !password) {
      res.writeHead(400);
      return res.end(JSON.stringify({ error: 'Missing fields' }));
    }

    const user = db.users.get(username);
    if (!user) {
      res.writeHead(401);
      return res.end(JSON.stringify({ error: 'Invalid credentials' }));
    }

    if (user.password !== password) {
      res.writeHead(401);
      return res.end(JSON.stringify({ error: 'Invalid credentials' }));
    }

    const token = createToken(username);
    res.writeHead(200);
    res.end(JSON.stringify({
      token,
      username,
      expiresIn: 86400000
    }));
  });
}

function handleGetUser(req, res) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Unauthorized' }));
  }

  try {
    // Mock token parsing - extract username from token payload
    const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64'));
    const username = payload.sub;
    
    if (!username || !db.users.has(username)) {
      res.writeHead(401);
      return res.end(JSON.stringify({ error: 'User not found' }));
    }

    const user = db.users.get(username);
    res.writeHead(200);
    res.end(JSON.stringify(user));
  } catch (e) {
    res.writeHead(401);
    res.end(JSON.stringify({ error: 'Invalid token' }));
  }
}

function handleUpdateUser(req, res) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Unauthorized' }));
  }

  try {
    const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64'));
    const username = payload.sub;
    
    if (!username || !db.users.has(username)) {
      res.writeHead(401);
      return res.end(JSON.stringify({ error: 'User not found' }));
    }

    readBody(req, (body) => {
      const { height, weight, age, gender } = body;
      const user = db.users.get(username);
      
      // Update user fields
      if (height !== undefined) user.height = height;
      if (weight !== undefined) user.weight = weight;
      if (age !== undefined) user.age = age;
      if (gender !== undefined) user.gender = gender;
      
      db.users.set(username, user);
      
      saveData();
      
      res.writeHead(200);
      res.end(JSON.stringify(user));
    });
  } catch (e) {
    res.writeHead(401);
    res.end(JSON.stringify({ error: 'Invalid token' }));
  }
}

function handleSearchFoods(req, res) {
  const query = url.parse(req.url, true).query;
  const search = query.search?.toLowerCase() || '';
  const limit = parseInt(query.limit) || 10;

  console.log(`🔍 handleSearchFoods: search="${search}", limit=${limit}`);

  // Search custom food database with case-insensitive matching
  const results = Array.from(db.foods.values())
    .filter(food => food.name.toLowerCase().includes(search.toLowerCase()))
    .slice(0, limit);

  res.writeHead(200);
  res.end(JSON.stringify(results));
}

function handleAddFood(req, res) {
  readBody(req, (body) => {
    const { name, calorie, note } = body;
    
    if (!name || !calorie) {
      res.writeHead(400);
      return res.end(JSON.stringify({ error: 'Missing fields' }));
    }

    const id = db.nextFoodId++;
    const food = {
      id,
      name: name.toLowerCase(),  // Store name in lowercase
      calorie: parseInt(calorie),
      note: note || '',
      createdAt: new Date().toISOString()
    };
    db.foods.set(id, food);
    
    saveData();

    res.writeHead(201);
    res.end(JSON.stringify(food));
  });
}

function handleUpdateFood(req, res) {
  readBody(req, (body) => {
    const foodId = parseInt(req.url.split('/').pop());
    const { name, calorie, note } = body;

    if (!db.foods.has(foodId)) {
      res.writeHead(404);
      return res.end(JSON.stringify({ error: 'Food not found' }));
    }

    const existingFood = db.foods.get(foodId);
    const food = {
      id: foodId,
      name: name ? name.toLowerCase() : existingFood.name,
      calorie: calorie || existingFood.calorie,
      note: note !== undefined ? note : existingFood.note,
      createdAt: existingFood.createdAt,
      updatedAt: new Date().toISOString()
    };
    db.foods.set(foodId, food);
    
    saveData();

    res.writeHead(200);
    res.end(JSON.stringify(food));
  });
}

function handleDeleteFood(req, res) {
  const foodId = parseInt(req.url.split('/').pop());
  if (db.foods.has(foodId)) {
    db.foods.delete(foodId);
    
    saveData();
    res.writeHead(204);
    res.end();
  } else {
    res.writeHead(404);
    res.end(JSON.stringify({ error: 'Food not found' }));
  }
}

function handleAddFoodLog(req, res) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Unauthorized' }));
  }

  let currentUsername;
  try {
    const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64'));
    currentUsername = payload.sub;
  } catch (e) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Invalid token' }));
  }

  readBody(req, (body) => {
    const { username, foods, foodName, calorie, note, date } = body;
    
    const demoUsername = username || currentUsername;
    const foodDate = date || getTodayEST();  // Use EST timezone for current date

    // Check if a food log already exists for this user and date
    let existingLog = Array.from(db.foodLogs.values()).find(log => 
      log.username === demoUsername && log.date === foodDate
    );

    let foodLog;
    
    if (existingLog) {
      // Add to existing food log
      foodLog = existingLog;
      
      if (foodName && calorie) {
        // Check if this food exists in database
        const normalizedFoodName = foodName.toLowerCase();
        let existingFood = Array.from(db.foods.values()).find(f => f.name === normalizedFoodName && f.calorie === calorie);
        
        // If food doesn't exist in database, create it
        if (!existingFood) {
          const foodId = db.nextFoodId++;
          existingFood = {
            id: foodId,
            name: normalizedFoodName,
            calorie: calorie,
            note: note || '',
            createdAt: new Date().toISOString()
          };
          db.foods.set(foodId, existingFood);
          console.log(`✅ Auto-created food in database: ${normalizedFoodName} (${calorie} cal)`);
        }

        // Add new food item to existing log
        foodLog.foods.push({
          id: Date.now().toString(),
          name: foodName,
          calorie: calorie,
          note: note || ''
        });
      }
    } else {
      // Create new food log
      const id = db.nextFoodLogId++;
      
      let foodItems = foods || [];
      if (foodName && calorie) {
        // Check if this food exists in database
        const normalizedFoodName = foodName.toLowerCase();
        let existingFood = Array.from(db.foods.values()).find(f => f.name === normalizedFoodName && f.calorie === calorie);
        
        // If food doesn't exist in database, create it
        if (!existingFood) {
          const foodId = db.nextFoodId++;
          existingFood = {
            id: foodId,
            name: normalizedFoodName,
            calorie: calorie,
            note: note || '',
            createdAt: new Date().toISOString()
          };
          db.foods.set(foodId, existingFood);
          console.log(`✅ Auto-created food in database: ${normalizedFoodName} (${calorie} cal)`);
        }

        foodItems = [{
          id: Date.now().toString(),
          name: foodName,
          calorie: calorie,
          note: note || ''
        }];
      }

      foodLog = {
        id: id.toString(),
        username: demoUsername,
        date: foodDate,
        foods: foodItems,
        totalCalories: 0
      };
      
      db.foodLogs.set(id, foodLog);
    }

    // Recalculate total calories
    foodLog.totalCalories = foodLog.foods.reduce((sum, f) => sum + f.calorie, 0);
    
    saveData();
    
    res.writeHead(201);
    res.end(JSON.stringify(foodLog));
  });
}

function handleGetFoodLogs(req, res) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Unauthorized' }));
  }

  let currentUsername;
  try {
    const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64'));
    currentUsername = payload.sub;
  } catch (e) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Invalid token' }));
  }

  const query = url.parse(req.url, true).query;
  const date = query.date;

  if (date) {
    // Get specific date logs for current user
    const logs = Array.from(db.foodLogs.values()).filter(log => {
      if (log.username !== currentUsername) return false;
      if (log.date !== date) return false;
      return true;
    });

    // Return first matching log or empty log for the date
    if (logs.length > 0) {
      res.writeHead(200);
      return res.end(JSON.stringify(logs[0]));
    } else {
      // Return empty food log for this date
      res.writeHead(200);
      return res.end(JSON.stringify({
        id: db.nextFoodLogId.toString(),
        username: currentUsername,
        date: date,
        foods: [],
        totalCalories: 0
      }));
    }
  }

  // Get all logs for user
  const logs = Array.from(db.foodLogs.values()).filter(log => log.username === currentUsername);

  res.writeHead(200);
  res.end(JSON.stringify(logs));
}

function handleUpdateFoodLog(req, res) {
  const foodLogId = parseInt(req.url.split('/').pop());
  console.log(`🔄 handleUpdateFoodLog: id=${foodLogId}`);
  
  readBody(req, (body) => {
    const { foodName, calorie, note, index } = body;

    if (!db.foodLogs.has(foodLogId)) {
      res.writeHead(404);
      return res.end(JSON.stringify({ error: 'Food log not found' }));
    }

    const foodLog = db.foodLogs.get(foodLogId);
    if (index !== undefined && index >= 0 && index < foodLog.foods.length) {
      // Update existing food item
      foodLog.foods[index] = {
        id: foodLog.foods[index].id,
        name: foodName,
        calorie: calorie,
        note: note || ''
      };
    }

    // Recalculate total
    foodLog.totalCalories = foodLog.foods.reduce((sum, f) => sum + f.calorie, 0);
    db.foodLogs.set(foodLogId, foodLog);
    
    saveData();

    res.writeHead(200);
    res.end(JSON.stringify(foodLog));
  });
}

function handleDeleteFoodLog(req, res) {
  const foodLogId = parseInt(req.url.split('/').pop().split('?')[0]);
  const query = url.parse(req.url, true).query;
  const index = parseInt(query.index);
  
  console.log(`🗑️  handleDeleteFoodLog: id=${foodLogId}, index=${index}`);

  if (!db.foodLogs.has(foodLogId)) {
    res.writeHead(404);
    return res.end(JSON.stringify({ error: 'Food log not found' }));
  }

  const foodLog = db.foodLogs.get(foodLogId);
  if (index >= 0 && index < foodLog.foods.length) {
    foodLog.foods.splice(index, 1);
    foodLog.totalCalories = foodLog.foods.reduce((sum, f) => sum + f.calorie, 0);
    
    // If no foods left, delete the entire log entry
    if (foodLog.foods.length === 0) {
      db.foodLogs.delete(foodLogId);
      console.log(`🗑️  Deleted entire food log (id=${foodLogId}) - no foods remaining`);
    } else {
      db.foodLogs.set(foodLogId, foodLog);
    }
    
    saveData();
  }

  res.writeHead(200);
  res.end(JSON.stringify(foodLog));
}

// Calculate Basal Metabolic Rate using Mifflin-St Jeor Equation
function calculateBMR(weight, height, age, gender) {
  let bmr;
  if (gender === 'male') {
    bmr = 10 * weight + 6.25 * height - 5 * age + 5;
  } else {
    bmr = 10 * weight + 6.25 * height - 5 * age - 161;
  }
  return bmr;
}

// Calculate daily calorie needs (assuming moderate activity)
function calculateTDEE(bmr) {
  return Math.round(bmr * 1.55); // Moderate activity level
}

// Calculate BMI
function calculateBMI(weight, height) {
  return weight / ((height / 100) ** 2);
}

function getBMIStatus(bmi) {
  if (bmi < 18.5) return 'Underweight';
  if (bmi < 25) return 'Normal';
  if (bmi < 30) return 'Overweight';
  return 'Obese';
}

function handleDashboardToday(req, res) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Unauthorized' }));
  }

  let currentUsername;
  try {
    const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64'));
    currentUsername = payload.sub;
  } catch (e) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Invalid token' }));
  }

  const today = getTodayEST();
  const logs = Array.from(db.foodLogs.values()).filter(log => log.date === today && log.username === currentUsername);
  
  const totalCalories = logs.reduce((sum, log) => sum + log.totalCalories, 0);
  const foodsLogged = logs.reduce((sum, log) => sum + log.foods.length, 0);

  // Get current user from token
  const currentUser = db.users.get(currentUsername);
  
  let suggestedDaily = 2000; // default
  let bmiValue = 22.5;
  let bmiStatus = 'Normal';

  if (currentUser && currentUser.weight && currentUser.height && currentUser.age) {
    const bmr = calculateBMR(currentUser.weight, currentUser.height, currentUser.age, currentUser.gender || 'other');
    suggestedDaily = calculateTDEE(bmr);
    bmiValue = calculateBMI(currentUser.weight, currentUser.height);
    bmiStatus = getBMIStatus(bmiValue);
  }

  res.writeHead(200);
  res.end(JSON.stringify({
    calorieTracking: {
      consumed: totalCalories,
      suggestedDaily: suggestedDaily,
      remaining: suggestedDaily - totalCalories,
      percentage: Math.round((totalCalories / suggestedDaily) * 100)
    },
    bmi: {
      value: bmiValue,
      status: bmiStatus
    },
    foodsLogged
  }));
}

function handleMonthlyStats(req, res) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Unauthorized' }));
  }

  let currentUsername;
  try {
    const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64'));
    currentUsername = payload.sub;
  } catch (e) {
    res.writeHead(401);
    return res.end(JSON.stringify({ error: 'Invalid token' }));
  }

  // Get actual monthly data from the database for current user (using EST timezone)
  const currentMonth = getCurrentMonthEST(); // YYYY-MM
  const logs = Array.from(db.foodLogs.values()).filter(log => {
    const logMonth = log.date.substring(0, 7);
    return logMonth === currentMonth && log.username === currentUsername;
  });

  if (logs.length === 0) {
    // No logs for this month, return empty response
    res.writeHead(200);
    return res.end(JSON.stringify({
      summary: {
        averageDailyConsumption: 0,
        highestDayCalories: 0,
        lowestDayCalories: 0,
        daysWithLogs: 0
      },
      dailyData: []
    }));
  }

  // Calculate actual stats from logs
  const dailyTotals = {};
  logs.forEach(log => {
    if (!dailyTotals[log.date]) {
      dailyTotals[log.date] = 0;
    }
    dailyTotals[log.date] += log.totalCalories;
  });

  const dailyData = Object.entries(dailyTotals).map(([date, calories]) => ({
    date,
    totalCalories: calories
  })).sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

  const totalCalories = dailyData.reduce((sum, day) => sum + day.totalCalories, 0);
  const calorieValues = dailyData.map(day => day.totalCalories);
  const averageDailyConsumption = totalCalories / dailyData.length;
  const highestDayCalories = Math.max(...calorieValues);
  const lowestDayCalories = Math.min(...calorieValues);

  res.writeHead(200);
  res.end(JSON.stringify({
    summary: {
      averageDailyConsumption,
      highestDayCalories,
      lowestDayCalories,
      daysWithLogs: dailyData.length
    },
    dailyData
  }));
}

// Cleanup unused foods (not used in 30 days)
function cleanupUnusedFoods() {
  const thirtyDaysAgo = new Date();
  thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
  
  let removedCount = 0;
  
  // Check which foods are in any food logs (used foods)
  const usedFoodIds = new Set();
  Array.from(db.foodLogs.values()).forEach(log => {
    log.foods.forEach(food => {
      // Find the food ID by matching name and calorie
      Array.from(db.foods.values()).forEach(dbFood => {
        if (dbFood.name === food.name.toLowerCase() && dbFood.calorie === food.calorie) {
          usedFoodIds.add(dbFood.id);
        }
      });
    });
  });
  
  // Remove foods not in any logs and older than 30 days
  Array.from(db.foods.entries()).forEach(([id, food]) => {
    const createdDate = new Date(food.createdAt);
    if (!usedFoodIds.has(id) && createdDate < thirtyDaysAgo) {
      db.foods.delete(id);
      removedCount++;
    }
  });
  
  if (removedCount > 0) {
    console.log(`🧹 Cleaned up ${removedCount} unused foods (not used in 30 days)`);
  }
}

// Run cleanup every 24 hours (86400000 ms)
setInterval(cleanupUnusedFoods, 24 * 60 * 60 * 1000);

// Also run cleanup on server start
function startServer() {
  cleanupUnusedFoods();
}

// Start server
const PORT = 3000;
const server = http.createServer(handleRequest);

// Load persisted data first
loadData();

// Initialize database with seed data if empty
if (db.foods.size === 0) {
  seedFoods();
}

// Seed mock monthly data if no food logs exist yet
if (db.foodLogs.size === 0) {
  seedMockMonthlyData();
}

server.listen(PORT, () => {
  startServer();
  console.log(`\n✅ Mock Backend Server running on http://localhost:${PORT}`);
  console.log(`✅ Frontend running on http://localhost:4200`);
  console.log(`✅ Food database: ${db.foods.size} foods, ${db.foodLogs.size} food logs`);
  console.log(`✅ Users: ${db.users.size} registered`);
  console.log(`✅ Cleanup: Foods unused for 30+ days will be removed daily`);
  console.log(`✅ Data: Persisted to ${path.basename(DATA_FILE)}`);
  console.log(`\nYou can now test the application!\n`);
});
