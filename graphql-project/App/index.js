import { ApolloServer } from "@apollo/server";
import { startStandaloneServer } from '@apollo/server/standalone';
import { typeDefs } from "./schema.js";
import db from "./_db.js";

const resolvers = {
  Query: {
    getReviews: async () => {
      try {
        const reviews = await db.getReviews();
        return reviews.map(review => ({
          id: review.review_id,
          rating: review.rating,
          review: review.review,
          reviewBy: review.review_by,
          reviewTime: review.review_time,
          profileUri: review.profile_uri
        }));
      } catch (error) {
        console.error('Error fetching reviews:', error);
        throw error;
      }
    }
  }
 ,
    Mutation: {
        insertReview: async (_, { rating, review, reviewBy, reviewTime, profileUri }) => {
            const reviewId = await db.insertReview(rating, review, reviewBy, reviewTime, profileUri);
            console.log('Inserted review ID:', reviewId);
            return reviewId;
        },
    },
};

// Server setup
const server = new ApolloServer({
    typeDefs,
    resolvers
});

// Start server
startStandaloneServer(server, {
    listen: { port: 4000 }
}).then(({ url }) => {
    console.log(`Server ready at ${url}`);
}).catch((error) => {
    console.error('Error starting server:', error);
});
