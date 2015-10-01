ROHAN Crawler
=============
### Required Components
- CRON _Optional, but very useful!_
- Postgres DB

## Usage
All the environment variables are stored in ```config.properites```, a sample of which is provided in ```config_sample.properties```.

The DB connector is written to talk directly with a Heroku Postgres Database via SSL (which allows for usage outside of the Herkou Cloud).
