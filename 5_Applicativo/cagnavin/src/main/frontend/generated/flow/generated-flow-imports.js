import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/dialog/src/vaadin-dialog.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/notification/src/vaadin-notification.js';
import '@vaadin/password-field/src/vaadin-password-field.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '0495c8f092eb16deb4565d1adeb18fbf52afb624a16e797d981af89fe1f2de8c') {
    pending.push(import('./chunks/chunk-c0e130ba52ffafff28f5437d4746e56a414de6977d7974cffe29df42efe3ab16.js'));
  }
  if (key === '889b0d7ce8273b5e2820d7dd467b5b23a4e86c94c13024a0bb96638abf1dfb5a') {
    pending.push(import('./chunks/chunk-e38f432c01a5d6742671c2b14eb3c13d88d662eb991e5e2acda3b98cf5626939.js'));
  }
  if (key === 'e665372ae3e122f1ea113a1c6cad37ee65ab3ce9ade37f0f24fd433e6e29bd3e') {
    pending.push(import('./chunks/chunk-2fff66ddb6ddb254fe90c2594c00b15d780da3fd4abfff04870700f3e1d530d0.js'));
  }
  if (key === '14cb82a2000d786313e7b642e8225261f924200b4f6983539c770e0770ebd094') {
    pending.push(import('./chunks/chunk-499856e4eae3724944c503bfe04b9a46cb30b9ea8d2cf68373e088d197992701.js'));
  }
  if (key === '7de5f216571e6af4a4671d97742357c42f87bbbdd937d4e5c82707e64120b589') {
    pending.push(import('./chunks/chunk-34ea7a12edb59a7b83b3e5f672880b8dc5db085a655849ba383b057c92277849.js'));
  }
  if (key === '813704e273c64c27e4ccdb5af4f82d803d3708fbbfe9ee5369aee60eed52cd39') {
    pending.push(import('./chunks/chunk-7bb6f394ea94a2f1a523eb6cc5c5a33339b53af1ab7b369c63820e7e0a77fae6.js'));
  }
  if (key === 'd3faec59b02e3c79495678dd0d1e32ab953a659e25bc43a7fad90a04477f215a') {
    pending.push(import('./chunks/chunk-562bcd481320a3ef67379ead9b6d0ecd5beff4cb1954f41575ecf3bf30de2e78.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}