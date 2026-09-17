# FoodFlow — Profile, Favorites, Reviews API

Base path: `/api/v1`

This document covers only the Ubay Saifee backend modules. The application currently accepts the related IDs in the request or URL. When the authentication module is integrated, it should derive `userId` from the authenticated user rather than trusting a client-supplied ID.

## User profile

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/users/{userId}/profile` | Create one profile for a user |
| `GET` | `/users/{userId}/profile` | Get the user's profile |
| `PUT` | `/users/{userId}/profile` | Update the user's profile |

Create or update body:

```json
{
  "fullName": "Ubay Saifee",
  "phoneNumber": "+919876543210",
  "address": "Food Street",
  "city": "Mumbai",
  "postalCode": "400001",
  "profileImageUrl": "https://example.com/avatar.png"
}
```

`fullName` is required. Only one profile can exist for a user.

## Favorite food

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/users/{userId}/favorites` | Add a food item to the user's favorites |
| `GET` | `/users/{userId}/favorites` | List a user's favorites |
| `DELETE` | `/users/{userId}/favorites/{foodId}` | Remove a favorite |

Add body:

```json
{ "foodId": 501 }
```

Each `(userId, foodId)` pair is unique.

## Reviews and ratings

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/reviews` | Create a review for a food item or restaurant |
| `GET` | `/reviews?targetType=FOOD&targetId=601` | List target reviews |
| `GET` | `/reviews/summary?targetType=RESTAURANT&targetId=201` | Get average rating and count |
| `PUT` | `/reviews/{reviewId}` | Update the review author's rating/comment |
| `DELETE` | `/reviews/{reviewId}?userId=103` | Delete the review author's review |

Create body:

```json
{
  "userId": 103,
  "targetType": "RESTAURANT",
  "targetId": 201,
  "rating": 5,
  "comment": "Excellent food and service."
}
```

`targetType` accepts `FOOD` or `RESTAURANT`; `rating` must be an integer from 1 through 5. A user may submit only one review for a specific target. Update and delete operations require the original review owner.

## Errors

Validation failures return `400`, missing records return `404`, and duplicate profiles, favorites, or reviews return `409`. Errors use a consistent JSON shape with `message` and (for validation errors) `validationErrors`.

## Database setup

The production datasource defaults to PostgreSQL and can be configured with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. Tests use an isolated in-memory H2 database.
