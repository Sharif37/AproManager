const express = require('express');
const bodyParser = require('body-parser');
const db = require('./db');



const app = express();


app.use(bodyParser.json());
app.use(logRequest); 




// Route to fetch reviews
app.get('/reviews', async (req, res) => {
    try {
        // Fetch reviews from the database
        const reviews = await db.getReviews();
        res.json(reviews); // Send the reviews as JSON response
    } catch (error) {
        console.error('Error fetching reviews:', error);
        res.status(500).json({ error: 'Internal server error' }); // Send an error response
    }
});

// Route to insert a new review
app.post('/reviews', async (req, res) => {
    const { rating, review, reviewBy, reviewTime, profileUri } = req.body;
        try {
            // Insert the new review into the database
            const reviewId = await db.insertReview(rating, review, reviewBy, reviewTime, profileUri);
            res.status(201).json({ id: reviewId }); 
        } catch (error) {
            console.error('Error inserting review:', error);
            res.status(500).json({ error: 'Internal server error' }); 
        }
    
});


function logRequest(req, res, next) {
    console.log(`Received ${req.method} request for ${req.url}`);
    console.log('Headers:', req.headers);
    console.log('Body:', req.body);
    next(); 
}



const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
    console.log(`Server listening on port ${PORT}`);
});


