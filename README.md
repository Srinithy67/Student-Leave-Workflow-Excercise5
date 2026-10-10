# Camunda Leave Request Workflow

This project models a simple leave/day-out request process using Camunda 8. Day-out requests are approved automatically, requests involving one or more nights are sent to the Warden for review in Tasklist, and requests with invalid dates are rejected.

## Setup and Run

Add your Camunda connection details to `src/main/resources/application.yaml`, then start the application with:

```bash
mvn spring-boot:run
```

## Process Model

The BPMN model for the workflow is shown below.

![Process Model](screenshots/modeler-diagram.png)

## Example Runs

### Test A: Automatic Approval

A day-out request follows the automatic approval path.

![Automatic Approval](screenshots/test-a-auto-approved.png)

### Test B: Warden Review

Requests covering one or more nights are routed to the Warden through Tasklist.

![Tasklist Form](screenshots/test-b-tasklist-form.png)

![Warden Approval Result](screenshots/test-b-warden-operate.png)

### Test C: Invalid Date Rejection

Requests with invalid dates are rejected by the process.

![Invalid Date Rejection](screenshots/test-c-rejected.png)
