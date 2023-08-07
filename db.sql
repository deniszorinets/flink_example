CREATE EXTENSION IF NOT EXISTS timescaledb;

CREATE TABLE japan_trade_stats_from_1988_to_2019 (
	ym timestamptz,
	import_export boolean,
	hs_code integer,
	customs integer,
	country char(9),
	q1 bigint,
	q2 bigint,
	value bigint
);

SELECT create_hypertable(
	'japan_trade_stats_from_1988_to_2019',
	'ym',
	chunk_time_interval => INTERVAL '1 month'
);