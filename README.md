# CSE-360-TeamProject
Team Project for CSE360 - ASU 

# Deliverables
### Project Deliverables - Links to relevant deliverables  
* [Team Project Report (Google Doc)](https://docs.google.com/document/d/1eKY1ra0Rp9Fvj6Kd1y0YryjP38ertm6kgclr2zKQdCg/edit?tab=t.0)
  (Current Phase Completed PDF turned in on Canvas)
- Demo Screentest - This screencast documents demonstration of successful testing, as well as a walk through demo of the project. [(Youtube.com)](https://www.youtube.com/watch?v=qjIEaaubhx4) 
- Technical ScreenCast - This screencast documents and explains the project's code. [(Youtube.com)](https://youtu.be/NwmCDx2oE-Y)
- [Standup Meeting 1](https://drive.google.com/file/d/1jHgE2P6WjzSbBd6ExgwVkffbZeLLi8B6/view?usp=sharing)
- [Standup Meeting 2](https://drive.google.com/file/d/1JQn_3-jU08iLCtDeEgJPndBQY38tpBL9/view?usp=sharing)
- [Standup Meeting 3](https://drive.google.com/file/d/1tImzZoB-6Zg0wOMdRcmKJxXVnvuCqLP-/view?usp=sharing)
- [Standup Meeting 4]() - No Link, meeting was held in text-based format due to time constraints + life responsibilities

- Team Norms: Included in the above google document 

- Project Source Code : ./src/
- Project Documents: ./docs/
- JavaDoc Documents: ./doc/
  - Collab guide for helping get eclipse to work
- Project Libs: ./libs/

### Project Phase3 Design Documents
* Introduction - A comprehensive overview of the software design of the Staff role, brief updates to the Instructor abilities and Admin role in the application.
* System Overview -
  * Desktop-based application with added Staff and Instructor role specifically to monitor all activity of posted questions and answers, communicating through a universal Q&A forum.
  * Instructors can send a request ticket to an admin, requesting to perform an only admin-action with a description for context/reasoning.
  * Admin will be able to manage requests tickets sent from Instructors and choose to perform the “open” request or not, marking it “closed” moving ticket to archives when completed/rejected.
  * Instructors, staff, and admin users will be able to view all archived “Closed” requests tickets, instructors being able to reopen a ticket with a description (for clarification).
  * Staff can moderate content posted, private messages exchanged, and be able to send warnings or feedback to any user as a result.
  * Instructors can establish and modify parameters for a Reviewer’s Scorecard to help adjust and calculate for more accuracy of a Reviewer user’s “credibility” score.
  * Developed using Java, JavaFX, H2 Database, MVC (Model-View-Controller) architecture.
* Design Considerations -
  * User-friendly interface and simple navigation to be able to easily send, moderate, and view ticket requests, easy readability of a Reviewer’s scorecard, clear difference between “closed” ticket request and “open” ticket request with context given. 
* Data Integrity/Authentication -
  * Instructor, staff and admin roles will be able to see archived request tickets that are marked as “closed”, only instructors will be able to reopen the ticket request and add in a description. Admin user role only be able to review request tickets and grant them/deny them. Instructor users can only be able to adjust and add a set of parameters to a Reviewer’s Scorecard.
* Specifications - 
  * Admins will be able to review a page of request tickets from instructors.
  * Admins will be able to see details of request tickets and mark them as “closed” if they decide to deny it or have already completed the task.
  * Instructors will be able to submit request tickets for admin to perform specific admin-only actions with a description text for context.
  * Instructors will be able to create and modify Reviewers user’s scorecard with set parameters.
  * Staff will be able to moderate all user’s posts and private messages.
  *  Staff, Instructors, and Administrators will be able to view a list of closed/achieved request tickets with their full context details.
  * Instructors will be able to reopen a request ticket with add-on detail description.
  * Admins will be able to review and decide again on reopened request tickets.
  
  
# How to make eclipse work!

I included the .jars and lib files for both h2 and JavaFX, but the .dlls are too large. You'll have to download javafx SDK 24.0.2, you probably already have it from HW1
--module-path "lib" --add-modules javafx.controls,javafx.fxml -Djava.library.path="C:\path\to\javafx-sdk-24.0.2\bin"
Update the path above to wherever the JavaFX SDK /bin folder is. 

