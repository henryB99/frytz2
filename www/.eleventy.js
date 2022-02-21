const eleventyNavigationPlugin = require("@11ty/eleventy-navigation");

const markdownIt = require('markdown-it');
const markdownItKbd = require('markdown-it-kbd');
const markdownItAnchor = require('markdown-it-anchor');
const markdownItContainer = require('markdown-it-container');
const pluginTOC = require('eleventy-plugin-toc');
const syntaxHighlight = require("@11ty/eleventy-plugin-syntaxhighlight");
const heroicons = require('eleventy-plugin-heroicons');

module.exports = function(eleventyConfig) {

  eleventyConfig.addPlugin(eleventyNavigationPlugin);
  eleventyConfig.addPlugin(heroicons);
  eleventyConfig.addPlugin(syntaxHighlight);

  eleventyConfig.addPassthroughCopy('src/assets')
  eleventyConfig.addPassthroughCopy('src/img')
  eleventyConfig.addPassthroughCopy({'src/icon': '.'})

  eleventyConfig.setBrowserSyncConfig({
    port: 9090,
    serveStatic: [
      {
        route: '/headless-demo',
        dir: '../headless-demo/build/distributions'
      },
      {
        route: '/api',
        dir: '../api'
      }]
  });

  const {
    DateTime
  } = require("luxon");

  // https://html.spec.whatwg.org/multipage/common-microsyntaxes.html#valid-date-string
  eleventyConfig.addFilter('htmlDateString', (dateObj) => {
    return DateTime.fromJSDate(dateObj, {
      zone: 'utc'
    }).toFormat('yyyy-MM-dd');
    });

    eleventyConfig.addFilter("readableDate", dateObj => {
    return DateTime.fromJSDate(dateObj, {
      zone: 'utc'
    }).toFormat("dd-MM-yyyy");
  });

  // Markdown
  eleventyConfig.setLibrary(
      'md',
      markdownIt()
          .use(markdownItAnchor)
          .use(markdownItKbd)
          .use(markdownItContainer, "info")
          .use(markdownItContainer, "warning")
  )

  eleventyConfig.addPlugin(pluginTOC, {
    tags: ['h2', 'h3', 'h4'], // which heading tags are selected headings must each have an ID attribute
    wrapper: 'nav',           // element to put around the root `ol`/`ul`
    wrapperClass: 'toc',      // class for the element around the root `ol`/`ul`
    ul: true,                // if to use `ul` instead of `ol`
    flat: false,
  })

  return {
    dir: { input: 'src', output: '_site' }
  };
};
