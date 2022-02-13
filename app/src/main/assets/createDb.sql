create Table local_charges
(charge_id        INTEGER PRIMARY KEY,
 remote_charge_id INTEGER,
 timestamp        TEXT,
 mileage          INTEGER,
 distance         INTEGER,
 charge_kw_paid   TEXT,
 price            TEXT,
 target_soc       INTEGER,
 charge_typ       TEXT,
 bc_consumption   TEXT
);