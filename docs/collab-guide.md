# 🚀 CSE-310 Team Project – Collaboration Guide

This guide explains how our team will use **GitHub** to collaborate on the project.  
Follow these steps to keep our repository clean, organized, and easy to grade.

---

## 🔑 Setup (First Time Only)

```bash
# Clone the repository
git clone https://github.com/YourTeamName/CSE-310-TeamProject.git
cd CSE-310-TeamProject

# Configure your Git identity (must match your GitHub email)
git config --global user.name "Your Name"
git config --global user.email "YourGitHubEmail@example.com"

# (Optional) Verify your config
git config --get user.name
git config --get user.email
```

> You can use GitHub’s **no-reply** email for privacy (see GitHub account settings).

---

## 🌲 Branching Workflow

We **never commit directly to `main`**.  
All work is done in **feature branches** and merged via **Pull Requests (PRs)**.

- **`main`** → always clean, submission-ready  
- **feature branches** → where individual work happens

### Create a new branch

```bash
git checkout main
git pull origin main        # ensure latest
git checkout -b feature-your-task
```

✅ Examples:
- `feature-input-validation`  
- `feature-user-stories`  
- `docs-team-norms`

---

## 📝 Making Changes

1. Edit your assigned files in VS Code.
2. Stage and commit your changes.
3. Push your branch to GitHub.

```bash
# Stage and commit
git add .
git commit -m "Short, clear summary of changes"

# Push (first push sets upstream)
git push -u origin feature-your-task
```

---

## 📄 Add a New File and Push It

```bash
# Create a file (example)
mkdir -p docs
echo "# Team Norms" > docs/team-norms.md

# Stage, commit, and push
git add docs/team-norms.md
git commit -m "Add team norms draft"
git push
```

---

## 🔄 Open a Pull Request (PR)

1. Go to your repo on **GitHub**.  
2. Click **“Compare & pull request.”**  
3. Fill out:
   - **Title**: brief summary  
   - **Description**: what changed, why, any notes for reviewers  
   - **Reviewers**: assign at least one teammate  
4. ✅ Wait for review and approval before merging.

---

## ✅ Merging & Staying Up to Date

After approval, the PR is merged into **`main`**.

Before starting new work, update your local copy and branch off again:

```bash
git checkout main
git pull origin main
git checkout -b feature-next-task
```

---

## 📋 Team Rules for GitHub

- Work on **feature branches** only  
- Write **clear, descriptive commit messages**  
- **Review PRs promptly** and leave constructive feedback  
- Keep `main` **clean** (no broken code)  
- (Optional) **Delete branches** after merge to stay tidy

---

## ⚡ Quick VS Code Tips

- **Source Control** tab → view changes, stage, commit  
- **✓ Commit** → requires a commit message  
- **Branch menu (bottom-left)** → switch/create branches  
- **Publish Branch** → pushes your branch to GitHub

---

## 🛠️ Troubleshooting (Quick Fixes)

```bash
# "Author identity unknown" when committing
git config --global user.name "Your Name"
git config --global user.email "YourGitHubEmail@example.com"

# "No upstream branch" when pushing
git push -u origin feature-your-task

# Need latest main before branching
git checkout main && git pull origin main
```

---

✅ Following this workflow helps us avoid conflicts, track changes clearly, and keep `main` submission-ready.
