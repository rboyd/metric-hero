# treemapper

Given a directory, outputs a JSON file in the format expected by the [d3
Treemap Layout](https://github.com/mbostock/d3/wiki/Treemap-Layout)

## Example

A 3d treemap of treemapper's own codebase:

![Treemapper Treemap](https://raw.github.com/wiki/rboyd/treemapper/images/treemapper-map.png)

## Usage

```clojure
(require 'treemapper.core)
(treemapper.core/analyze "work/treemapper")
```

* Copy output.json into resources/public
* Start sinatra: ```ruby serv.rb```
* Visit ```http://localhost:4567/index.html```
* Navigate with WASD, QE, RF, and Arrow Keys


## License

Copyright © 2013 Robert Boyd

Distributed under the Eclipse Public License, the same as Clojure.
