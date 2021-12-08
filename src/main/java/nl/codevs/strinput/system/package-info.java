/**
 * StrInput system implementation.
 *
 * <ul>
 *     <li>{@link nl.codevs.strinput.system.StrCenter}
 *     the beating heart of any command system.<br>
 *     Implement this and call
 *     {@link nl.codevs.strinput.system.StrCenter#onCommand(
 *     java.util.List, nl.codevs.strinput.system.StrUser)
 *     } with command details.</li>
 *     <li>{@link nl.codevs.strinput.system.StrUser}
 *     Make an implementation for your platform of this class.<br>
 *     All senders must implement this, so the system can interact.<br>
 *     You can add extra fields, see the class' documentation.</li>
 *     <li>{@link nl.codevs.strinput.system.StrCategory}
 *     All command categories (with methods as commands),
 *     implement this interface.</li>
 *     <li>{@link nl.codevs.strinput.system.StrSettings}
 *     Contain the default and non-text-configurable settings.</li>
 *     <li>{@link nl.codevs.strinput.system.StrInput} and
 *     {@link nl.codevs.strinput.system.Param} are annotations
 *     used to indicate commands / categories and command parameters
 *     respectively. All commands, categories and parameters must be annotated
 *     by these.</li>
 *     <li>{@link nl.codevs.strinput.system.Env}
 *     contains environment variables such as the
 *     {@link nl.codevs.strinput.system.StrCenter} and
 *     {@link nl.codevs.strinput.system.StrUser} currently
 *     active on the thread.</li>
 * </ul>
 * @author Sjoerd van de Goor
 */
package nl.codevs.strinput.system;
