drop table  IF EXISTS Category CASCADE;
drop table  IF EXISTS Users CASCADE;
drop table  IF EXISTS Location CASCADE;
drop table  IF EXISTS Event_state CASCADE;
drop table  IF EXISTS Compilation CASCADE;
drop table  IF EXISTS Events CASCADE;
drop table  IF EXISTS Compilation_events CASCADE;
drop table  IF EXISTS participation_requests CASCADE;


CREATE TABLE IF NOT EXISTS Category
( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, name varchar(50)
);

CREATE TABLE IF NOT EXISTS Users
( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, email varchar(254)
, name varchar(250)
);

CREATE TABLE IF NOT EXISTS Location
( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, lat FLOAT
, lon FLOAT
);

CREATE TABLE IF NOT EXISTS Event_state
( id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, state varchar(40)
);

INSERT INTO Event_state (state)
values
('PENDING'),
('PUBLISHED'),
('CANCELED'),
('CONFIRMED'),
('REJECTED');


CREATE TABLE IF NOT EXISTS Compilation
( id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, Pinned boolean
, title varchar(50)
);


CREATE TABLE IF NOT EXISTS Events
( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, annotation varchar(2000)
, confirmed_requests bigint
, created_on timestamp
, description varchar(7000)
, event_date timestamp
, paid boolean
, participant_limit integer
, published_on timestamp
, request_moderation boolean
, title varchar(200)
, views bigint
, category_id bigint
, user_id bigint
, location_id bigint
, event_state_id INTEGER,
CONSTRAINT fk_category_to_events FOREIGN KEY(category_id) REFERENCES Category (id),
CONSTRAINT fk_users_to_events FOREIGN KEY(user_id) REFERENCES Users(id),
CONSTRAINT fk_locations_to_events FOREIGN KEY(location_id) REFERENCES location(id),
CONSTRAINT fk_states_to_events FOREIGN KEY(event_state_id) REFERENCES event_state(id)
);

CREATE TABLE IF NOT EXISTS Compilation_events
( id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, Compilation_id BIGINT
, Events_id BIGINT,
CONSTRAINT fk_Compilation_line_to_Events FOREIGN KEY(Compilation_id) REFERENCES Compilation(id),
CONSTRAINT fk_requests_line_items FOREIGN KEY(Events_id) REFERENCES Events(id));

CREATE TABLE IF NOT EXISTS participation_requests
( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, created timestamp
, event_id bigint
, user_id bigint
, status_id bigint,
CONSTRAINT fk_events_to_participation_requests FOREIGN KEY(event_id) REFERENCES Events (id),
CONSTRAINT fk_users_to_participation_requests FOREIGN KEY(user_id) REFERENCES Users(id),
CONSTRAINT fk_status_to_participation_requests FOREIGN KEY(status_id) REFERENCES Event_state(id)
);
