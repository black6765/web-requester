# Simple API Web Requester
## Requirements
- OS : Windows, Mac, Linux
- JDK(JRE) : 17
## Usage
- This is a web-based API tool similar to [Postman](https://www.postman.com/) or [Thunder Client for VS Code](https://www.thunderclient.com/).
![image](https://github.com/user-attachments/assets/c911922b-9b55-418a-9368-1e3bf416bff1)
- You can request with [GET, DELETE, POST, PUT] HTTP method.
  - If you input body, JSON code editor([CodeMirror](https://codemirror.net/) based) available.
  - The response also printed in json format
    - Support code highlighting and code folding fuction.
- Each request(item) can be managed separately into Collection and Workplace.
  - Management features such as create, delete, modify, copy for Collections(collection, workspace, item) are supported.
- Environment variables support
  - Environment variables can be used when entering URLs using the {{input}} syntax.
