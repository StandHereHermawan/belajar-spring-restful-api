# User API Spec

## Register User

Endpoint : POST /api/users

Request Body :

```json
{
  "username" : "arief_mencoba_teknologi",
  "password" : "rahasia",
  "name" : "Arief Karditya Hermawan"
}
```

Response body (Success) :

```json
{
  "data" : "OK"
}
```

Response body (Failed) :

```json
{
  "errors" : "Username must not blank, ???"
}
```

## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
  "username" : "arief_mencoba_teknologi",
  "password" : "rahasia"
}
```

Response body (Success) :

```JSmin
{
  "data" : {
    "token" : "TOKEN",
    "expiredAt" : 1000000000000000000 // millisecond
  }
}
```

Response body (Failed, 401) :

```json
{
  "errors" : "Username or password wrong"
}
```

## Get User

Endpoint : GET /api/users/current

Request Header :

- X-API TOKEN : Token (Mandatory)

Response body (Success) :

```JSmin
{
  "data" : {
    "username" : "arief_mencoba_teknologi",
    "expiredAt" : 1000000000000000000 // millisecond
  }
}
```

Response body (Failed, 401) :

```json
{
  "errors" : "Unauthorized"
}
```

## Update User

Endpoint : PATCH /api/users/current

Request Header :

- X-API TOKEN : Token (Mandatory)

Request Body :
 ```JSmin
{
  "name" : "Arief Karditya Hermawan", // put if only want to update name
  "password" : "new password" // put if only want to update password
}
 ```

Response body (Success) :

```json
{
  "data" : {
    "username" : "arief_mencoba_teknologi",
    "name" : "Arief Karditya Hermawan"
  }
}
```

Response body (Failed, 401) :

```json
{
  "errors" : "Unauthorized"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header :

- X-API TOKEN : Token (Mandatory)

Response body (Success) :

```json
{
  "data" : "OK"
}
```
