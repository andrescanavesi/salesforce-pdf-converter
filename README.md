# Salesforce advanced pdf converter

This example shows how to use an org to convert html to advanced pdf using a Java client (Maven project)
All we know renderAs="pdf" but now Salesforce has an advanced pdf converter: renderAs="advanced_pdf".

Still this functionality is in pilot what It means that you need to create a case and someone from support will activate It in your org.

"Advanced PDF renders Visualforce pages as PDF files with broader support for modern HTML standards, such as CSS3, JavaScript, and HTML5.
This change applies to both Lightning Experience and Salesforce Classic."
[Read more](https://releasenotes.docs.salesforce.com/en-us/summer17/release-notes/rn_vf_advanced_pdf.htm)

Once activated you can test It by creating a Visualforce page like this:

```html
<apex:page sidebar="false" showHeader="false" renderAs="advanced_pdf">
    <html lang="en-US" >
        <head>
            <meta charset="UTF-8" />
        </head>
        <body>
            <h1>Hello, world!</h1>
        </body>
    </html>
</apex:page>
```

## Set up

You will need an Apex class that exposes a RESTful webservice to convert html to advanced pdf

```java
@RestResource(urlMapping='/htmltopdf')
global class HtmlToPdf {
    @HttpPost
    global static PdfResponse convertToPdf(String html) {
        browser.RenderRequest request = new browser.RenderRequest();
        request.setPageContent(html);
        Blob blobVal = browser.Browser.renderToPdf(request);
        PdfResponse resp = new PdfResponse();
        resp.base64Content = EncodingUtil.base64Encode(blobVal);
        return resp;
    }

    global class PdfResponse {
        String base64Content;
    }
}
```

Ideally you should create a user with enough permissions to execute that class.

### Java client configuration

Create a connected app in your org to get client id and client secret.
Use the scope "Access and manage your data (api)" and any callback url.
After that you must update Config class;

```java
    public static final String SALESFORCE_PDF_CONVERTER_USERNAME = "";
    /**
     * Password + access token
     */
    public static final String SALESFORCE_PDF_CONVERTER_PASSWORD = "";
    public static final String SALESFORCE_PDF_CONVERTER_CLIENT_ID = "";
    public static final String SALESFORCE_PDF_CONVERTER_CLIENT_SECRET = "";
```

Finally execute the Main class. It will send the following html to your org and and save
the pdf locally

```html
<!DOCTYPE html>
<html>
    <head>
        <title>Pdf test</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <div>PDF test...</div>
    </body>
</html>
```

## Important
By default remote css, images and any other resource won't work. I recommend you upload
all in a static resource and do references using relative path.

If you want to use remote resources you must create a Remote Setting in your org and of course pdf conversion will be slower.
