# CSE-360-TeamProject
Team Project for CSE360 - ASU 

# Deliverables
### Project Deliverables - Links to relevant deliverables  
- [Team Project Report (Google Doc)](https://docs.google.com/document/d/1uQYse7CRU5mB1yXKWRj2MBDQrIfw3NvYD8Fvsl5CsXs/edit?usp=sharing)
- Team Project Report - Current Phase Completed PDF 
- Standup 1 Link: https://drive.google.com/file/d/1z9gdqxR9_6wXilsDHyN-EHbCyQWYLQht/view?usp=sharing
- Standup 2 Link: https://drive.google.com/file/d/1vGqnoTy7fKNoPD9SVSekAnb1D0HYfBO9/view?usp=sharing
- User-Facing Screencast 1 Link: 
- Technical Screencast 2: https://youtu.be/JabwQ2pI4WQ
- Team Norms: https://docs.google.com/document/d/1uQYse7CRU5mB1yXKWRj2MBDQrIfw3NvYD8Fvsl5CsXs/edit?tab=t.wykrdagzvxyi
- 

- Project Source Code : ./src/
- Project Documents: ./docs/
  - Collab guide for helping get eclipse to work
- Project Libs: ./libs/
  
  


# How to make eclipse work!

I included the .jars and lib files for both h2 and JavaFX, but the .dlls are too large. You'll have to download javafx SDK 24.0.2, you probably already have it from HW1
--module-path "lib" --add-modules javafx.controls,javafx.fxml -Djava.library.path="C:\path\to\javafx-sdk-24.0.2\bin"
Update the path above to wherever the JavaFX SDK /bin folder is. 

