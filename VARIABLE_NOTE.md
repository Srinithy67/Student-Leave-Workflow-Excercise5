# Variable note - Hostel Out-Pass workflow

Process ID: hostel-outpass | Form ID: warden-approval-form

## Start variables (from POST /outpass)
studentName, regNo, hostelBlock, outDate, returnDate, parentPhone, purpose

## Worker: check-outpass  (service task "Check request")
Reads : regNo, outDate, returnDate
Writes: nights   = days between outDate and returnDate
        valid    = false when returnDate is before outDate
        approved = true only when valid and nights = 0 (day out, auto-approved)

## User task: Warden approval (form warden-approval-form)
Reads (displayed): studentName, regNo, hostelBlock, outDate, returnDate, nights, purpose, parentPhone
Writes: approved (checkbox), wardenRemarks (text area)

## Worker: issue-outpass  (service task "Issue out-pass")
Reads : regNo, outDate, studentName, parentPhone, approved, wardenRemarks
Writes: passStatus = ISSUED or REJECTED
        passNumber = OP-<regNo>-<outDate> (only when ISSUED)
Side effect: logs "SMS to parent ..." with the decision.

## Gateway conditions
=valid and nights = 0    -> merge (auto-approve)
=valid and nights >= 1   -> Warden approval
=not(valid)              -> merge (reject)
