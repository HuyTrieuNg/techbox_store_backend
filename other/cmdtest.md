Chạy Dockertest với env.test

` docker-compose -f docker-compose.test.yml --env-file .env.test up -d `


Chạy spring pofile test 

` .\mvnw.cmd spring-boot:run -D"spring-boot.run.profiles=test" `
