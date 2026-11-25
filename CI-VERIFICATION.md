# Documentation-only CI verification sample

This temporary sample exists to exercise pull request and linked-issue CI
verification. It must not be merged.

## Scope

- Only this Markdown file is added.
- Recipes, source revisions, layer configuration, and runtime behavior are unchanged.
- No component release, image deployment, or device operation is requested.

## Verification expectations

Confirm the pull request targets `develop` and is linked to exactly one issue.
Report actual CI results separately from dispatch acceptance. Documentation-only
changes may be skipped by build selection; a skipped build is not a passed build.

Leave the sample pull request and issue open for inspection.
