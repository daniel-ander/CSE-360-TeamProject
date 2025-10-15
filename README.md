# CSE-360-TeamProject
Team Project for CSE360 - ASU 

# Deliverables
### Project Deliverables - Links to relevant deliverables  
- [Team Project Report (Google Doc)](https://docs.google.com/document/d/1EsmSLK5tWXNMgdoVfmusR9ti-TLOBsUrhgj01S7Vnts/edit?tab=t.0)
- Team Project Report - Current Phase Completed PDF Turned In on Canvas
- All links to screencasts and standups links are contained in the above google document
- Team Norms: Included in the above google document 

- Project Source Code : ./src/
- Project Documents: ./docs/
  - Collab guide for helping get eclipse to work
- Project Libs: ./libs/
  
  
# How to make eclipse work!

I included the .jars and lib files for both h2 and JavaFX, but the .dlls are too large. You'll have to download javafx SDK 24.0.2, you probably already have it from HW1
--module-path "lib" --add-modules javafx.controls,javafx.fxml -Djava.library.path="C:\path\to\javafx-sdk-24.0.2\bin"
Update the path above to wherever the JavaFX SDK /bin folder is. 

