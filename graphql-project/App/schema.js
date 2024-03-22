export const typeDefs = `#graphql
type Review {
  id: Int!
  rating: Float
  review: String
  reviewBy: String
  reviewTime: String
  profileUri: String
}

type Query {
  getReviews: [Review]
}

type Mutation {
  insertReview(rating: Float, review: String, reviewBy: String, reviewTime: String, profileUri: String): Review
}

`;
