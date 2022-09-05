
# sca-change-of-circumstances

This is the repository for Change of Circumstances Backend

Frontend: https://github.com/hmrc/sca-change-of-circumstances-frontend

Stub: https://github.com/hmrc/sca-change-of-circumstances-stub

Requirements
------------

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.

## Run the application

To update from Nexus and start all services from the RELEASE version instead of snapshot
```
sm --start SCA_CHANGE_OF_CIRCUMSTANCES_ALL -r
```

### To run the application locally execute the following:
```
sm --stop SCA_CHANGE_OF_CIRCUMSTANCES
```
and
```
sbt 'run 10601'
```
### Using the application
To log in using the Authority Wizard please click the link below:

http://localhost:9949/auth-login-stub/gg-sign-in

and fill in these details below

redirect URL : http://localhost:10600/change-of-circumstances
NINO : AA999999A or AA999999B or AA999999C

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").