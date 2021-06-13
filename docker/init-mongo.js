db.createUser(
    {
        user: "root",
        pwd: "root",
        roles: [
            {
                role: "admin",
                db: "my_mongo"
            }
        ]
    }
)