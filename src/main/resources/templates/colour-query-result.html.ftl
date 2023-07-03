<#ftl encoding="utf-8">
<#import "./core.ftl" as core>
<!DOCTYPE html>
<html lang = "en">
  <head>
    <@core.meta query/>
    <style>
      <@core.style/>
      body {
        background-color: ${colour};
      }
      .result {
        display: flex;
        justify-content: center; 
        color: #eeeeee;
        font-family: sans-serif;
        font-size: 18px;
        padding-top: 64px;
      }    
    </style>
  </head>
  <body>
    <@core.script/>
    <@core.query query/>
    <div class="result">
      <table>
        <#list table as row>
          <tr>
            <#list row as cell>
              <td>
                ${cell.text}
              </td>
           </#list>              
          </tr>
        </#list>
      </table>
    </div>
  </body>
</html>