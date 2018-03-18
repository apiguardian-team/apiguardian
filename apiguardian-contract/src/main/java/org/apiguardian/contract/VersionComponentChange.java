package org.apiguardian.contract;

/**
 * Enumeration describing the difference between two versions, assumed both are compliant with basic Semantic Versioning
 * format. Labels and other metadata (like snapshot, pre-release, etc) are ignored.
 * <p>
 * <strong>Comparability</strong><br/>
 * Enum values are ordered from more specific to more general. If a version has changed in two ways, use the more
 * general one (e.g. if version 1.0.0 has changed to 2.1.0, do NOT use <code>MINOR</code>, but rather <code>MAJOR</code>).
 * In other words, use the value that describes first component change when reading versions from left to right.
 * <br/>
 * <code>Comparable<...></code> is implemented here in that way - given <code>VersionComponentChange c1</code> and
 * <code>VersionComponentChange c2</code>, then <code>c1.compareTo(c2) > 0</code>, meaning that <code>c1</code> is more
 * general than <code>c2</code>, e.g. <code>c1 = MAJOR</code> and <code>c2 = MINOR</code>.
 * @see <a href="https://semver.org/">Semantic Versioning</a>
 */
//todo is "most specific" and "most general" good naming for version change "size"?
// it seems quite natural to say that MAJOR version change is more general than MINOR, but I'm not sure whether
// MINOR is more specific than MAJOR; still, I can't find a better phrasing for now
// would that change, remember to propagate it to APIVersioningContract javadocs too
public enum VersionComponentChange {
    /**
     * No version component has changed.
     * <p>
     * This does not mean that versions are equal - just that no component has changed.
     * <p>
     * Example:
     * <ul>
     *     <li>1.0.0 \u21e8 1.0.0</li>
     *     <li>0.0.1-SNAPSHOT \u21e8 0.0.1</li>
     * </ul>
     */
    NONE,
    /**
     * Minor version component has changed.
     * <p>
     * Examples:
     * <ul>
     *     <li>1.0.0 \u21e8 1.0.1</li>
     *     <li>0.0.1 \u21e8 0.0.2</li>
     * </ul>
     */
    PATCH,
    /**
     * Minor version component has changed.
     * <p>
     * Examples:
     * <ul>
     *     <li>2.0.0 \u21e8 2.1.0</li>
     *     <li>1.4.1 \u21e8 1.5.2</li>
     *     <li>2.5.5 \u21e8 2.6.0</li>
     *     <li>0.0.1 \u21e8 0.1.0</li>
     * </ul>
     */
    MINOR,
    /**
     * Major version component has changed.
     * <p>
     * Examples:
     * <ul>
     *     <li>2.0.0 \u21e8 3.0.0</li>
     *     <li>1.4.1 \u21e8 2.0.0</li>
     *     <li>2.5.0 \u21e8 3.1.0</li>
     *     <li>0.1.0 \u21e8 1.0.0</li>
     * </ul>
     */
    MAJOR
}
