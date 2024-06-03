const express = require('express');
const path = require('path');
const app = express();
const port = 3000;

app.use(express.json());
app.use(express.static('public'));

let todos = [];
let id = 1;

// Serve the index.html on the root URL
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'views', 'index.html'));
});

// Create a new to-do
app.post('/todos', (req, res) => {
    const { task } = req.body;
    if (!task) {
        return res.status(400).json({ error: 'Task is required' });
    }
    const newTodo = { id: id++, task, completed: false };
    todos.push(newTodo);
    res.status(201).json(newTodo);
});

// Get all to-dos
app.get('/todos', (req, res) => {
    res.json(todos);
});

// Get a single to-do by ID
app.get('/todos/:id', (req, res) => {
    const todo = todos.find(t => t.id === parseInt(req.params.id));
    if (!todo) {
        return res.status(404).json({ error: 'To-do not found' });
    }
    res.json(todo);
});

// Update a to-do by ID
app.put('/todos/:id', (req, res) => {
    const { task, completed } = req.body;
    const todo = todos.find(t => t.id === parseInt(req.params.id));
    if (!todo) {
        return res.status(404).json({ error: 'To-do not found' });
    }
    if (task !== undefined) todo.task = task;
    if (completed !== undefined) todo.completed = completed;
    res.json(todo);
});

// Delete a to-do by ID
app.delete('/todos/:id', (req, res) => {
    todos = todos.filter(t => t.id !== parseInt(req.params.id));
    res.status(204).end();
});

app.listen(port, () => {
    console.log(`To-Do app listening at http://localhost:${port}`);
});
