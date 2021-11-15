# Sample Spring Cloud Function AWS
## Build
```shell
mvn clean package
```

## Deploy/Run
Using AWS Management Console:
1. Create Function
   1. Author from scratch
   2. Give it a name
   3. Runtime Java 11
   4. Set/change default execution role appropriately
   5. Click "Create Function"
2. Upload code
   1. Choose "Upload from" | ".zip or .jar"
   2. Choose the `function-sample-aws-2.0.0.RELEASE-aws.jar`
   3. Click "Upload"
3. In the Runtime settings, click edit
   1. Set "Handler" to: `org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest`
   2. Click Save
4. Choose the "Test" tab
   1. Use input located in `src/test/resources/input.json`
   2. Save changes
   3. Click "Test"
   4. Expand "Execution result" | "Details" to view the result