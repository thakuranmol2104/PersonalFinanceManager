# Personal Finance Manager

JavaFX desktop application for managing personal income, expenses, budgets, and reports.

## Features

- Add and delete income or expense transactions
- Set category-wise budgets
- Track spent, remaining budget, and exceeded limits
- View a styled dashboard summary, category reports, and monthly totals
- Persist data using CSV files in the `data/` folder
- Apply modern UI styling using JavaFX and CSS

## Project Structure

- `src/personalfinance/model` - model classes
- `src/personalfinance/manager` - business logic
- `src/personalfinance/storage` - CSV file handling
- `src/personalfinance/ui` - JavaFX user interface and stylesheet

## Run In VS Code

1. Open this folder in VS Code.
2. Make sure JDK 25 is selected.
3. Install a JavaFX SDK locally, then note the `lib` folder path.
4. Compile:

```powershell
$env:PATH_TO_FX="C:\path\to\javafx-sdk\lib"
javac --module-path $env:PATH_TO_FX --add-modules javafx.controls -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

5. Run:

```powershell
java --module-path $env:PATH_TO_FX --add-modules javafx.controls -cp out personalfinance.Main
```

If you do not have JavaFX SDK installed yet, the code is ready but it will not compile until the SDK path is available.
