- Log to the postgresql console using the 'postgres' user:
     - sudo -u postgres psql

- Run a SQL script:
     - Outside of the PostgreSQL console:
          - sudo -u postgres
          - psql -f file_with_sql.sql
               - To wrap all commands in transaction:
                    - psql --single-transaction -f file_with_sql.sql
     - Inside of the PostgreSQL console:
          - sudo -u postgres psql
          - \i file_with_sql.sql

- Tips:
     - From 'psql' connect to another database:
          - \connect DBNAME
     - From 'psql' see where are the configuration files:
          - SHOW config_file;
     - Start/stop the PostgreSQL db:
          - Start:
               - systemctl start postgresql.service
          - Stop:
               - systemctl stop postgresql.service

- Check PostgreSQL port:
     - sudo netstat -plunt | grep postgres
