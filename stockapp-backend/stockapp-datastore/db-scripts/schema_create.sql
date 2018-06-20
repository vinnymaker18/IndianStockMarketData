-- Create the user table. This also creates 2 indices on this table, 
-- one by user_id cause it's the primary key and another by username
-- cause of the unique constraint.
CREATE TABLE users (user_id serial primary key,
                    password_hash varchar(256) not null,
                    password_salt varchar(256) not null,
                    username varchar(64) unique not null,
                    date_created timestamp without time zone not null);

-- Create the exchange table.
CREATE TABLE exchanges (exchange_id serial primary key,
					    title varchar(256) not null,
					    code varchar(256) unique not null);

-- Create the stocks table.
CREATE TABLE stocks (stock_id serial primary key,
					 exchange_id integer references exchanges(exchange_id),
					 symbol varchar(32) not null,
					 open real default -1.0
					 ltp real default -1.0
					 high real default -1.0
					 low real default -1.0
					 prev_close real default -1.0);

