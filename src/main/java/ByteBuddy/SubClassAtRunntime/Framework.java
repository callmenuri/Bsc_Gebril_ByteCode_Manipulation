package ByteBuddy.SubClassAtRunntime;

/**
 * Das Interface Framework dient primär dazu, die Implementierung des Sicherheitsmechanismus flexibel und austauschbar zu machen
 */
interface Framework {
    <T> T secure(Class<T> type);
}
