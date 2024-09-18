<#ftl encoding="utf-8">
<#import "./core.ftl" as core>
<!DOCTYPE html>
<html lang = "en">
  <head>
    <@core.meta query/>
    <style>
      <@core.style/>
      .result {
        display: flex;
        flex-direction: column;
        justify-content: center; 
        color: #eeeeee;
        font-family: sans-serif;
        font-size: 18px;
        padding-left: 32px;
        padding-right: 32px;
        padding-top: 32px;
      }    
    </style>
  </head>
  <body>
    <@core.script/>
    <@core.query query/>
    <div class="result">
      <h2>${word}</h2>
      <table>
        <#list entries as entry>
          <tr>
            <td>            
              <i>${entry.partOfSpeech}</i>
              <ol>              
                <#list entry.definitions as definition>
                  <li>${definition}</li>
                </#list>
              </ol>
            </td>
          </tr>
        </#list>
      </table>
    </div>
  </body>
</html>
