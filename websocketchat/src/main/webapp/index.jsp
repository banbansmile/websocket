<%@ page language="java" pageEncoding="UTF-8" %>
<html>
  <head>
    <title>Web Client</title>
    <link rel="stylesheet" type="text/css" href="css/index.css" />
    <script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <meta charset="UTF-8">
  </head>
  <body>
    <div class="frame">
      <div class="controls">
        <div class="control-pane">
          <table>
            <tr>
              <th>Server Connect Setting:</th>
              <td class="status"></td>
            </tr>
            <tr>
              <th>URL:</th>
              <td><input type="text" id="url" value="ws://localhost:6666/" /></td>
            </tr>
            <tr>
              <th>UserName:</th>
              <td><input type="text" id="name" value="maoge" /></td>
            </tr>
            <tr>
              <th></th>
              <td>
                <input type="submit" id="btn_connect" value="Connect" />
              </td>
            </tr>
          </table>
        </div>
     
        <div class="control-pane">
          <table>
            <tr>
              <th style="text-align: left;">Send Message</th>
            </tr>
            <tr>
              <td><textarea id="message_text"></textarea></td>
            </tr>
            <tr>
              <td><input type="submit" id="btn_send" value="Send" /></td>
            </tr>
          </table>
        </div>
      </div> <!-- /controls -->
      <div class="log">
        <input type="submit" id="btn_clear" value="Clear" />
        <div class="entries" id="content">
        </div>
      </div> <!-- /log -->
    </div> <!-- /frame -->
  </body>
</html>