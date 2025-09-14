# CSE-310 Team Project – Collaboration Guide

This guide explains how our team will use GitHub to collaborate on the project.  
Please follow these steps to keep our repository clean, organized, and easy to grade.

---

## 🔑 Setup

1. Clone the repository (first time only):
   git clone https://github.com/YourTeamName/CSE-310-TeamProject.git
   cd CSE-310-TeamProject

2. Configure Git identity (first time only):
   git config --global user.name "Your Name"
   git config --global user.email "YourGitHubEmail@example.com"

   > Use the same email linked to your GitHub account.  
   > You can find a no-reply GitHub email in your account settings if you prefer privacy.

---

## 🌲 Branching Workflow

We will never commit directly to `main`.  
Instead, each task is completed in its own branch and merged through a Pull Request (PR).

- Main branch (`main`) → always clean, submission-ready.  
- Feature branches → where individual work is done.  

### Create a new branch
   git checkout main
   git pull origin main        # get the latest code
   git checkout -b feature-your-task

Examples:
- feature-input-validation  
- feature-user-stories  
- docs-team-norms  

---

## 📝 Making Changes

1. Edit your assigned files in VS Code.  
2. Stage and commit:
   git add .
   git commit -m "Short description of what you changed"
3. Push your branch to GitHub:
   git push origin feature-your-task

---

## 🔄 Pull Requests (PRs)

1. After pushing, go to GitHub.  
2. Click "Compare & pull request".  
3. Fill out:
   - Title: Brief summary of the change  
   - Description: What was changed, notes for reviewers  
   - Assign at least one teammate as reviewer  
4. Wait for review before merging.  

---

## ✅ Merging & Staying Up To Date

- After approval, the PR is merged into `main`.  
- Before starting new work:
   git checkout main
   git pull origin main
   git checkout -b feature-next-task

This ensures you are always working from the latest version.  

---

## 📋 Team Rules for GitHub

- Always work on a feature branch.  
- Use clear, descriptive commit messages.  
- Review teammate PRs promptly.  
- Keep `main` clean — no broken code.  
- Delete branches after merge (optional but keeps things tidy).  

---

## ⚡ Quick VS Code Tips

- Source Control tab (left sidebar) shows changed files.  
- Use ✓ to commit after writing a message.  
- Use bottom-left branch menu to switch or create branches.  
- After committing, click "Publish Branch" to push it to GitHub.  

---

Following this workflow will help us avoid conflicts, track changes clearly, and make integration much smoother.
