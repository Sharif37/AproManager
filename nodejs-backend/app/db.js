const { Pool } = require('pg');

class Database {
    constructor() {
        if (!Database.instance) {
            this.pool = new Pool({
                user: 'sharif',
                host: 'localhost',
                database: 'apromanager',
                password: 'csecu',
                port: 5432,
            });
            this.connect();
            Database.instance = this;
        }
        return Database.instance;
    }


    async connect() {
        try {
            await this.pool.connect();
            console.log('Connected to PostgreSQL database');
        } catch (error) {
            console.error('Error connecting to PostgreSQL database:', error);
        }
    }

    async query(text, params) {
        const client = await this.pool.connect();
        try {
            return await client.query(text, params);
        } finally {
            client.release();
        }
    }   




    async insertReview(rating, review, reviewBy, reviewTime, profileUri) {
        const query = `
            INSERT INTO reviews (rating, review, review_by, review_time, profile_uri)
            VALUES ($1, $2, $3, $4, $5)
            RETURNING review_id;
        `;
        const values = [rating, review, reviewBy, reviewTime, profileUri];

        try {
            const result = await this.query(query, values);
            return result.rows[0].review_id;
        } catch (error) {
            console.error('Error inserting review:', error);
            throw error;
        }
    }



    async getReviews() {
        const query = 'SELECT * FROM reviews';
        try {
            const result = await this.query(query);
            console.log(result.rows)
            return result.rows;
        } catch (error) {
            console.error('Error fetching reviews:', error);
            throw error;
        }
    }
    

}

module.exports = new Database();

