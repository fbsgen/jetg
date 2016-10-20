## Compiled template engine for java
[![Build Status](https://semaphoreci.com/api/v1/dyu/jetg/branches/master/badge.svg)](https://semaphoreci.com/dyu/jetg)

Build requirements:
- java 1.6 or higher
- maven 3.2 or higher

Credits:
 - [@subchen](https://github.com/subchen) - author of https://github.com/subchen/jetbrick-template-1x where this project was based from.

The goal of the modifications was to make it syntactically and semantically similar to how I generate code via ST4.

### Overview
- control chars (escape using backslash) :
  
  ```
  #
  «
  »
  ```
- Comments
  
  ```
  ## this is a comment
  ```
- Comment block
  
  ```
  #--
  this is a comment
  --#
  ```
- Procs
  - a.k.a reusable stateless functions (public static)
  - ```proc.jetg```
  
  ```
  «test("Doe", "John", 10)»
  
  #test (String lastName, String firstName, int age)
  name: «lastName», «firstName»
  age: «age»
  #end
  ```
  
- Imports
  - the imported procs are namespaced using the proc's filename with ```::``` as separator similar to c++
  - the ```.jetg``` suffix is inferred
  
  ```
  #import proc
  #import ./proc.html
  #set(ln = "Doe")

  «proc::test(ln, "John", 10)»
  «proc_html::test(ln, "Jane", 11)»
  «test(ln, "Joe", 12)»

  #test (String lastName, String firstName, int age)
      name: «lastName», «firstName»
      «age(age)»
  #end
  
  #age(int age)
  age: «age»
  #end
  ```
- Iteration similar to ST4
  
  ```
  «some_string_list:String:test("John", 10); separator="\n"»  
  ```
  The difference is the presence of ```:String``` which indicates the type of the first argument being passed to the proc

### Usage
```java
Properties props = new Properties();
props.put(JetConfig.COMPILE_DEBUG, "false");
props.put(JetConfig.TEMPLATE_LOADER, ClasspathResourceLoader.class.getName());
props.put(JetConfig.TEMPLATE_PATH, "/");
props.put(JetConfig.COMPILE_STRATEGY, "auto");
props.put(JetConfig.TEMPLATE_SUFFIX, ".jetg");
props.put(JetConfig.COMPILE_PATH, "target/generated-sources/jetg");

// initialize engine (reuse this instance)
JetEngine engine = JetEngine.create(props);

// loaded from classpath (reuse this instance)
JetTemplate template = engine.getTemplate("templates/stocks.jetg");

// fill
HashMap<String, Object> context = new HashMap<String, Object>();
List<foo.Stock> items = getItemsFromSomewhere();
context.put("items", items);

// render
StringWriter writer = new StringWriter();
template.render(context, writer);

System.out.println(writer.toString());
```

### template
stocks.jetg
```html
#define (List<foo.Stock> items)
«main(items)»
#main(List<foo.Stock> items)
<!DOCTYPE html>
<html>
  <head>
    <title>Stock Prices</title>
  </head>
  <body>
    <h1>Stock Prices</h1>
    <table>
      <thead>
        <tr>
          <th>#</th>
          <th>symbol</th>
          <th>name</th>
          <th>price</th>
          <th>change</th>
          <th>ratio</th>
        </tr>
      </thead>
      <tbody>
        «for(foo.Stock item : items)»
        «item_detail(item, item$$i + 1)»
        «endfor»
      </tbody>
    </table>
  </body>
</html>
#end
#item_detail(foo.Stock item, int count)
<tr class="«count % 2 == 0 ? "even" : "odd"»">
  <td>«count»</td>
  <td>
    <a href="/stocks/«item.symbol»">«item.symbol»</a>
  </td>
  <td>
    <a href="«item.url»">«item.name»</a>
  </td>
  <td>
    <strong>«item.price»</strong>
  </td>
  «if(item.change < 0.0)»
  <td class="minus">«item.change»</td>
  <td class="minus">«item.ratio»</td>
  «else»
  <td>«item.change»</td>
  <td>«item.ratio»</td>
  «endif»
</tr>
#end
```

### pojo
Stock.java
```java
package foo;

public final class Stock
{
    private String name;
    private String name2;
    private String url;
    private String symbol;
    private double price;
    private double change;
    private double ratio;

    public Stock(String name, String name2, String url, String symbol, 
            double price, double change, double ratio)
    {
        this.name = name;
        this.name2 = name2;
        this.url = url;
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.ratio = ratio;
    }

    public String getName()
    {
        return this.name;
    }

    public String getName2()
    {
        return this.name2;
    }

    public String getUrl()
    {
        return this.url;
    }

    public String getSymbol()
    {
        return this.symbol;
    }

    public double getPrice()
    {
        return this.price;
    }

    public double getChange()
    {
        return this.change;
    }

    public double getRatio()
    {
        return this.ratio;
    }
}
```
