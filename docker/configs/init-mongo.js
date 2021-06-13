db.createUser(
    {
        user: "root",
        pwd: "root",
        roles: [
            {
                role: "admin",
                db: "application"
            }
        ]
    }
);

db.application.insertOne(
    {
        "_class": "topic",
        "kafkaTopic": "main",
        "stompDestination": "main",
        "users": []
    }
);