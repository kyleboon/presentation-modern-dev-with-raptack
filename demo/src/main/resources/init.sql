DROP TABLE IF EXISTS blacklist;
CREATE TABLE blacklist (
  `id` bigint auto_increment primary key,
  `host` varchar(256)
)