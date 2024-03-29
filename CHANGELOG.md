## v4.0.0b8 (2021-10-11)

### Fix

- minor bugs

### Feat

- replace reactTo() with method level @Reactive (#34)

### Refactor

- make @Reactive method only (#42)

## v4.0.0b7 (2021-09-26)

### Feat

- list proxy support & proxy access rework (#45)

## 4.0.0b6 (2021-09-25)

### Refactor

- unify package structure between modules (#44)

## v4.0.0b5 (2021-09-23)

### Refactor

- make ProxyCreator instance based
- replace javassist ith bytebuddy
- use jigsaw modules
- rename missleading names
- extend abstraction for observers (#37)
- add abstraction for observers (#37)

### Feat

- add parameterless events/listeners

## v3.1.0b4 (2021-08-07)

### Feat

- replace @Reactive in UI (#33)

## v3.1.0b3 (2021-08-06)

### Feat

- implement "live object proxies"

## v3.1.0b2 (2021-07-21)

### Fix

- bug for #31

## v3.1.0b1 (2021-05-29)

### Feat

- super bindings
- add delete button in list example

### Fix

- deleting issue in `person list example`
- returning null on instanciation instead of throwing exception
- theoretical possible wrong react call
- Observer reporting changes on rebinding

### Refactor

- make properties of internal classes unreactive

### Perf

- change default listener capacity

## v3.1.0b0 (2021-01-20)

### Fix

- possible NullPointerException for FIELD_CACHE

### Feat

- add pseudo proxy

### Refactor

- **swing**: extract swing to different libary

## v3.0.2 (2021-01-18)

### Fix

- missing "removeById" reaction

## v3.0.1 (2021-01-18)

### Fix

- reactive list ID check

## v3.0.0 (2021-01-08)

### Refactor

- **codestyle**: apply PWD rules

### Feat

- **style**: extract util methods from ReactiveComponent
- **structure**: reworked listener model and structure

## v2.4.0 (2021-01-08)

### Fix

- **list**: add missing generic for list container
- expose missing ReactiveController.updateModel()
- **reflection**: private protected and package method not accessible with @Reactive

### Feat

- add unbindAll to reactable

### Refactor

- replace string concat by String.format
- adapt isFitting method to behave as expected
- replaced fori by for each
- add mvc package

## v2.2.2-SNAPSHOT (2020-11-24)

## v2.2.1-SNAPSHOT (2020-11-23)

## v2.1.2 (2020-11-23)

## v2.1.1 (2020-11-19)

## v2.0.1 (2020-11-17)

## v2.0.0-SNAPSHOT (2020-11-15)

## v1.1.0-SNAPSHOT (2020-11-07)

## v1.0.0 (2020-11-03)
