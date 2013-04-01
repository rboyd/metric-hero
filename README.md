# metric hero

> The hero is one who kindles a great light in the world, who sets up blazing torches in the dark streets of life for men to see by.

> -- *Felix Adler*

Metric hero analyzes your codebase and builds interactive visualizations. Use it to more effectively learn your way around a new codebase, or to focus your efforts on evolving existing projects.


## Example

A 3d treemap of metric hero's own codebase:

![Metric Hero Treemap](https://raw.github.com/wiki/rboyd/metric-hero/images/treemapper-map.png)

## Usage

```clojure
(require 'treemapper.core)
(treemapper.core/analyze "/path/to/repo")
```

* Copy output.json into resources/public
* Start jetty: ```lein run```
* Visit ```http://localhost:3000/```
* Navigate with WASD, QE, RF, and Arrow Keys


## License

Copyright © 2013 Robert Boyd

Distributed under the Eclipse Public License, the same as Clojure.
