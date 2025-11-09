# CSE-360-TeamProject
Team Project for CSE360 - ASU 

# Deliverables
### Project Deliverables - Links to relevant deliverables  
* [Team Project Report (Google Doc)](https://docs.google.com/document/d/1eKY1ra0Rp9Fvj6Kd1y0YryjP38ertm6kgclr2zKQdCg/edit?usp=sharing)
  (Current Phase Completed PDF turned in on Canvas)
- Demo Screentest - This screencast documents demonstration of successful testing, as well as a walk through demo of the project. [(Youtube.com)]() 
- Technical ScreenCast - This screencast documents and explains the project's code. [(Youtube.com)]()
- [Standup Meeting 1]()
- [Standup Meeting 2](https://drive.google.com/file/d/1JQn_3-jU08iLCtDeEgJPndBQY38tpBL9/view?usp=sharing)
- [Standup Meeting 3]()
- [Standup Meeting 4]()

- Team Norms: Included in the above google document 

- Project Source Code : ./src/
- Project Documents: ./docs/
  - Collab guide for helping get eclipse to work
- Project Libs: ./libs/

### Project Phase3 Design Documents
* Introduction - A comprehensive overview of the software design of the Reviewer role, brief updates in the Instructor and Student role in relation to the Reviewer role in the application.
* System Overview -
  * Desktop-based application with added Reviewer role specifically to be able to post “review” posted questions and answers, communicating through a universal forum. Reviewers would have the same ability to only edit and delete their own posts, and would have their own profile to show all their review posts.
  * Students will be able to private message reviewers related to their review posts to the student’s posts for clarification, etc. They can also  assign weightage values to reviewers so most trusted would appear top/only in the displayed forum list, utilized as a filter for the search bar and as a post hierarchy in forum threads. 
  * Students will be able to request permission to users with an Instructor role to be granted abilities of being a Reviewer, while Instructors can manage Student users (granting/rejecting) who requested permission to become a Reviewer, being able to look through all their posts (posted questions and answers).
  * Developed using Java, JavaFX, H2 Database, MVC (Model-View-Controller) architecture.
* Design Considerations - User-friendly interface for simple navigation to be able to easily view history posts of specific users, private viewing to messages sent to targeted users alike a universal communication feature, action to send messages, post, edit, delete, and have more specific search filters for questions/answers/reviews.
* Data Integrity/Authentication - Only the original poster of the question/answer will be able to edit or delete their posts. Private messaging features will only be seen by the two communicating parties for each “conversation thread”. Differentiation feature to tell a part of a reviewer and student (question poster or answer response). Reviewer(s) will have their own specific profile for others to see all their posts history.
* Specifications - 
  * Reviewers will be able to view, create, delete, and modify posts of their posted reviews.
  * Reviewers will be able to view the list of review posts they have written and check if they have private messages from the authors of questions/answers they reviewed.
  * Students will be able to read reviews posts from the Reviewers and only the authors will have the ability to choose to private message the Reviewer for clarification, etc.
  * Students will be able to create and modify a list of “trusted reviewers” and utilize it to filter searches and question threads that would prioritize showing specific reviewers posts.
  * Students will be able to add a weightage value to each reviewer that can determine the order of multiple reviews displayed, having the most useful/trusting reviews shown first.
  * Students will be able to request permission to also become a reviewer.
  * Instructors will be able to review a specific student’s history post to determine whether to grant or deny their request to become a reviewer. 
  
  
# How to make eclipse work!

I included the .jars and lib files for both h2 and JavaFX, but the .dlls are too large. You'll have to download javafx SDK 24.0.2, you probably already have it from HW1
--module-path "lib" --add-modules javafx.controls,javafx.fxml -Djava.library.path="C:\path\to\javafx-sdk-24.0.2\bin"
Update the path above to wherever the JavaFX SDK /bin folder is. 

