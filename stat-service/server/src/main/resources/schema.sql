drop table  IF EXISTS Endpoint_Hits CASCADE;
CREATE TABLE IF NOT EXISTS Endpoint_Hits
 (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
  , app varchar(320)
  , uri varchar(320)
  , ip varchar(320)
  , timestamp timestamp
);
