db.createUser(
    {
        user: "kafkadmin",
        pwd: "painfulkafka",
        roles: [
            {
                role: "readWrite",
                db: "my_mongo"
            }
        ]
    }
)