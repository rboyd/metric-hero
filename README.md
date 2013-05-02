# Metric Hero

> The hero is one who kindles a great light in the world, who sets up blazing torches in the dark streets of life for men to see by.

> -- *Felix Adler*

Metric Hero analyzes your codebase and builds interactive visualizations. Use it to more effectively learn your way around a new codebase, or to focus your efforts on evolving existing projects.


## Example

A Metric Hero render of the [Ruby on Rails](https://github.com/rails/rails) codebase:

![Rails Render](https://raw.github.com/wiki/rboyd/metric-hero/images/rails.png)

## Usage

```clojure
(require 'metric-hero.core)
(metric-hero.core/analyze "/path/to/repo" :ignore [#"ignored" #"regex"])
```

* Copy output.json into resources/public
* Compile ClojureScript ```lein cljsbuild once```
* Start jetty: ```lein run```
* Visit ```http://localhost:3000/```
* Navigate with WASD, QE, RF, and Arrow Keys


## License

Copyright © 2013 Robert Boyd

Distributed under the Eclipse Public License, the same as Clojure.
