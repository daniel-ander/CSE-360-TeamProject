# 🚀 CSE-360 Eclipse + GitHub Quick Guide

# 1. Clone the repo
git clone https://github.com/YourTeamName/CSE-360-TeamProject.git
cd CSE-360-TeamProject

# 2. Import into Eclipse
## File → Import → Existing Projects into Workspace
## Select the repo folder → Finish

# 3. Set JDK
## Project → Properties → Java Build Path → Libraries
## Make sure JRE System Library points to JDK 23

# 4. Run Config Setup (one-time)
## Run → Run Configurations → Java Application
## Main class: your project main (e.g., StartCSE360)
## VM arguments:
--module-path "lib" --add-modules javafx.controls,javafx.fxml -Djava.library.path="C:\path\to\javafx-sdk-24.0.2\bin"

# 5. Branching Workflow
git checkout main
git pull origin main
git checkout -b feature-your-task

# 6. Commit & Push
git add .
git commit -m "Short summary of changes"
git push -u origin feature-your-task

# 7. Pull Request
# On GitHub → Compare & pull request → Assign reviewer → Merge after approval
