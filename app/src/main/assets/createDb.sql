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
 bc_consumption   TEXT,
 charge_data_id   INTEGER
);

create Table remote_charges
(charge_id              INTEGER PRIMARY KEY,
 vehicle_vin            TEXT,
 charging_status        TEXT,
 timestamp              TEXT,
 start_of_charge        TEXT,
 end_of_charge          TEXT,
 quantity_charged       TEXT,
 average_charging_power TEXT,
 soc_start              INTEGER,
 soc_end                INTEGER,
 mobile_charge_id       INTEGER
);
